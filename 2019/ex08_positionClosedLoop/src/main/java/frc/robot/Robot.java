/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  // create xbox controller object
  private XboxController xbox = new XboxController(0);

  TalonSRX elevator = new TalonSRX(1);

  /** Used to create string thoughout loop */
   StringBuilder _sb = new StringBuilder();
   int _loops = 0;

   /** Track button state for single press event */
   boolean _lastButton1 = false;  

  	/** Save the target position to servo to */
  double targetPositionRotations;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    /* lets grab the 360 degree position of the MagEncoder's absolute position */
    /* mask out the bottom12 bits, we don't care about the wrap arounds */    
		int absolutePosition = elevator.getSelectedSensorPosition(0) & 0xFFF;
    
    /* use the low level API to set the quad encoder signal */
    elevator.setSelectedSensorPosition(absolutePosition, Constants.kPIDLoopIdx, Constants.kTimeoutMs);

    /* choose the sensor and sensor direction */
    elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 
        Constants.kPIDLoopIdx, Constants.kTimeoutMs);

    /* Ensure sensor is positive when output is positive */
    elevator.setSensorPhase(false);

    /**
    * Set based on what direction you want forward/positive to be.
    * This does not affect sensor phase. 
    */ 
    //elevator.setInverted(Constants.kMotorInvert);
    
    /* Config the peak and nominal outputs, 12V means full */
    elevator.configNominalOutputForward(0, Constants.kTimeoutMs);
    elevator.configNominalOutputReverse(0, Constants.kTimeoutMs);
    //elevator.configPeakOutputForward(1, Constants.kTimeoutMs);
    elevator.configNominalOutputForward(0.5);
    //elevator.configPeakOutputForward(1, Constants.kTimeoutMs);
    elevator.configPeakOutputForward(0.5);
    elevator.configPeakOutputReverse(-1, Constants.kTimeoutMs);
    
    /**
    * Config the allowable closed-loop error, Closed-Loop output will be
    * neutral within this range. See Table in Section 17.2.1 for native
    * units per rotation.
    */
    //elevator.configAllowableClosedloopError(0, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
    
    /* Config Position Closed Loop gains in slot0, typically kF stays zero. */
    elevator.config_kF(Constants.kPIDLoopIdx, 0.0, Constants.kTimeoutMs);
    elevator.config_kP(Constants.kPIDLoopIdx, 0.5, Constants.kTimeoutMs);
    elevator.config_kI(Constants.kPIDLoopIdx, 0.0, Constants.kTimeoutMs);
    elevator.config_kD(Constants.kPIDLoopIdx, 0.0, Constants.kTimeoutMs);


  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    //This prints Data to the Driver Station
    SmartDashboard.putString("DB/String 2", "" + elevator.getSensorCollection().getQuadraturePosition());
    //SmartDashboard.putString("DB/String 7", "" + rightEncoder.getDistance());
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
		/* xbox processing */
		double leftYstick = xbox.getY(Hand.kLeft);
		boolean buttonA = xbox.getAButton();
    boolean buttonB = xbox.getBButton();
    boolean buttonX = xbox.getXButton();
    boolean buttonY = xbox.getYButton();

		/* Get Talon/Victor's current output percentage */
		double motorOutput = elevator.getMotorOutputPercent();

		/* Deadband gamepad */
		if (Math.abs(leftYstick) < 0.10) {
			/* Within 10% of zero */
			leftYstick = 0;
    }

		/* Prepare line to print */
		_sb.append("\tout:");  _sb.append((int) (motorOutput * 100));              _sb.append("%");  // Percent
		_sb.append("\tpos:");  _sb.append(elevator.getSelectedSensorPosition(0));  _sb.append("u");  // Native units

		/**
		 * When button 1 is pressed, perform Position Closed Loop to selected position,
		 * indicated by Joystick position x10, [-10, 10] rotations
		 */
		//if (!_lastButton1 && buttonA) {
    if (buttonB) {
			/* Position Closed Loop */
			/* 10 Rotations * 4096 u/rev in either direction */
			targetPositionRotations = 5000; // 2* 4096; //leftYstick * 10.0 * 4096;
			elevator.set(ControlMode.Position, targetPositionRotations);
		}
    if (buttonX) {
			/* Position Closed Loop */
			/* 10 Rotations * 4096 u/rev in either direction */
			targetPositionRotations = 3000; //2* 4096; //leftYstick * 10.0 * 4096;
			elevator.set(ControlMode.Position, -targetPositionRotations);
		}

		/* When button 2 is held, just straight drive */
		if (buttonA) {
      /* Percent Output */
		  elevator.set(ControlMode.PercentOutput, leftYstick);
    }
    
    if (buttonY) {
      elevator.setSelectedSensorPosition(0); // reset to zero
      elevator.getSensorCollection().setQuadraturePosition(0, 10);
      elevator.set(ControlMode.PercentOutput, 0);
    }
//    else{
    //}

		/* If Talon is in position closed-loop, print some more info */
		if (elevator.getControlMode() == ControlMode.Position) {
      /* Append more signals to print when in speed mode. */
			_sb.append("\terr:");  _sb.append(elevator.getClosedLoopError(0));  _sb.append("u");	// Native Units
			_sb.append("\ttrg:");  _sb.append(targetPositionRotations);         _sb.append("u");	// Native Units
		}

		/**
		 * Print every ten loops, printing too much too fast is generally bad
		 * for performance.
		 */
		if (++_loops >= 10) {
			_loops = 0;
			System.out.println(_sb.toString());
		}

		/* Reset built string for next loop */
		_sb.setLength(0);
		
		/* Save button state for on press detect */
		//_lastButton1 = buttonA;
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
