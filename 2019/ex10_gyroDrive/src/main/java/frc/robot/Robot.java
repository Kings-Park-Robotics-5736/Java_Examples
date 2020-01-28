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
//import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Talon;
//import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

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

  private final int THRESHOLD = 3;
  private final double kP = 0.50;


  // create xbox controller object
  private XboxController xbox = new XboxController(0);

  // setup drive train
  private Talon leftMotor  = new Talon(0);
  private Talon rightMotor = new Talon(1);
  private DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor);

  // encoders to sense the wheel spin to determine distance
  //private Encoder leftEncoder    = new Encoder(2, 3);
  //private Encoder rightEncoder   = new Encoder(0, 1);
                                    // portA, portB, counting direction, 4x accuracy
  //private Encoder leftEncoder    = new Encoder(2, 3, false, Encoder.EncodingType.k4X);
  //private Encoder rightEncoder   = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
  
  // new gyro object
  private ADXRS450_Gyro gyro = new ADXRS450_Gyro();
  
  double rotation = 0;
  double fwd = 0;
  
  void reset() {
    // reset drive train
    drive.tankDrive(0, 0);
    drive.stopMotor();
    // reset encoders
    //leftEncoder.reset();
    //rightEncoder.reset();
	// reset gyro
    gyro.reset();
  }
  void forward(double x) { drive.tankDrive( x, x); }
  void right(double x)   { drive.tankDrive( x,-x); }
  void left(double x)    { drive.tankDrive(-x, x); }
  void reverse(double x) { drive.tankDrive(-x,-x); }

  void execute() {
    // Execute function that should be called every loop
    drive.tankDrive(fwd, rotation);

    fwd = 0;
    rotation = 0;
  }

  boolean rotateToAngle(double targetAngle) {
    double error = targetAngle - gyro.getAngle();  // # check out wpilib documentation for getting the angle from the gyro

    System.out.println("targetAngle: " + targetAngle + " gyro.getAngle():" + gyro.getAngle());
    //System.out.println("gyro.getAngle(): " + gyro.getAngle());

    if  (error > THRESHOLD) {
      //rotation =  error*kP;
      return true;
    }
    else {
//      rotation = 0;
      return false;
    }
  }

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
	
    // drive train specific initialization
    drive.setSafetyEnabled(false);

    // the encoder measures the wheel rotation.
    // set distancePerPulse(1), then push the wheel for 1 rotation and record the value.
    // I got 120, c=2piR => 3.14*6" wheels => 18.84/120 => new distance per pulse.
    // leftEncoder.setDistancePerPulse(1);
    // rightEncoder.setDistancePerPulse(1);
    //leftEncoder.setDistancePerPulse(18.84/120);   // Sets the scale factor between pulses and distance.
    //rightEncoder.setDistancePerPulse(18.84/120);  // Sets the scale factor between pulses and distance.

    gyro.calibrate();

    // initialize all values
    reset();
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
    //SmartDashboard.putString("DB/String 2", "" + leftEncoder.getDistance());
    //SmartDashboard.putString("DB/String 7", "" + rightEncoder.getDistance());
    SmartDashboard.putString("DB/String 3", "" + gyro.getAngle());
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

	// reset sensor
    reset();
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
    // POV in degrees -- North is 0 degrees
    if (xbox.getPOV() != -1) {
      System.out.println("xbox.getPOV() = " + xbox.getPOV());
      int direction = xbox.getPOV();
      if (direction == 0) {
        forward(0.50);
      } else if (direction == 90) {
        right(0.50);
      } else if (direction == 270) {
        left(0.50);
      } else if (direction == 180) {
        reverse(0.50);
      }
    }
    else {
      drive.stopMotor();
    }

    if (xbox.getAButton()) {
      if (rotateToAngle(90)) {
        right(0.5);
      } else {
        drive.stopMotor();
      }
    }
    if (xbox.getYButton()) {  // start button
      gyro.reset();
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
