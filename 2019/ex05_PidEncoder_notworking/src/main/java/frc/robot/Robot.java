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
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;


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

  // setup drive train
  private Talon leftMotor  = new Talon(0);
  private Talon rightMotor = new Talon(1);
  private DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor);

  // encoders to sense the wheel spin to determine distance
  private Encoder leftEncoder    = new Encoder(2, 3);
  private Encoder rightEncoder   = new Encoder(0, 1);
                                    // portA, portB, counting direction, 4x accuracy
  //private Encoder leftEncoder    = new Encoder(2, 3, false, Encoder.EncodingType.k4X);
  //private Encoder rightEncoder   = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
  private PIDController leftPid  = new PIDController(1, .01, 0, leftEncoder, leftMotor);
  private PIDController rightPid = new PIDController(1, .01, 0, rightEncoder, rightMotor);

  void reset() {
    // reset drive train
    drive.tankDrive(0, 0);
    drive.stopMotor();
    // reset encoders
    leftEncoder.reset();
    rightEncoder.reset();
  }
  void forward(double x) { drive.tankDrive( x, x); }
  void right(double x)   { drive.tankDrive( x,-x); }
  void left(double x)    { drive.tankDrive(-x, x); }
  void reverse(double x) { drive.tankDrive(-x,-x); }

  void drive_straight_enc(double speed)
  {
    double kP = 1;
    double error = leftEncoder.getDistance() - rightEncoder.getDistance();
    double rotation = 1; //kP * error;
    drive.arcadeDrive(speed, rotation, false);
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
    // auto start the camera streaming
    CameraServer.getInstance().startAutomaticCapture();  

    // the encoder measures the wheel rotation.
    // set distancePerPulse(1), then push the wheel for 1 rotation and record the value.
    // I got 120, c=2piR => 3.14*6" wheels => 18.84/120 => new distance per pulse.
    // leftEncoder.setDistancePerPulse(1);
    // rightEncoder.setDistancePerPulse(1);
    leftEncoder.setDistancePerPulse(18.84/120);   // Sets the scale factor between pulses and distance.
    rightEncoder.setDistancePerPulse(18.84/120);  // Sets the scale factor between pulses and distance.

    // PID controller 
    leftPid.reset();
    rightPid.reset();

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
    SmartDashboard.putString("DB/String 2", "" + leftEncoder.getDistance());
    SmartDashboard.putString("DB/String 7", "" + rightEncoder.getDistance());
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
    // RightTrigger (axis-3) = forward, left trigger (axis-2) = reverse and axis-0 (x) = left/right
    drive.arcadeDrive(xbox.getTriggerAxis(Hand.kRight) - xbox.getTriggerAxis(Hand.kLeft), xbox.getX(Hand.kLeft));

    // using the encoder, the robot should drive straight for 24 inches.
    if(xbox.getXButtonPressed())
    {
      leftPid.enable(); 
      rightPid.enable();
    //  while (leftPid.isEnabled() && rightPid.isEnabled())
	  {
         leftPid.setSetpoint(24);
         rightPid.setSetpoint(-24);
	  }
      drive_straight_enc(0.5);  // range 0-1
    }
    else if (xbox.getXButtonReleased())
    {
      //Gta Drive
      leftPid.disable();
      leftPid.reset();
      rightPid.disable();
      rightPid.reset();
    }
    if (xbox.getYButtonPressed())
    {
      leftEncoder.reset();
      rightEncoder.reset();
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
