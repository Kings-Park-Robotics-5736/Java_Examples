/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/**
 * Use the lime light in combination with the turret to track the target
 * In manual mode:
 *   - the RightTrigger (axis-3) = clockwise, left trigger (axis-2) = counter-clockwise
 * In tracking mode:
 *   - Use the left bumper (button 4) to do auto-tracking
 */

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// xbox specific files
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
// motor controller specific files
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
// import limelight network tables
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

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

  // constants
  private static final double Kp = 0.015;         // Proportional control constant
  private static final double min_command = 0.1;  // tweak this number to reduce oscillation

  private XboxController xbox = new XboxController(0);  // create xbox controller object
  private WPI_TalonSRX turret = new WPI_TalonSRX(5);    // create motor ctrl object
  //
  // setup network table for limelight
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry tv = table.getEntry("tv");

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
	
    // initialize the motor controller
    turret.setInverted(false);  // pick clockwise (CW) versus CCW when motor controller is positive/green
    turret.setNeutralMode(NeutralMode.Brake); // apply deceleration to combat the spinning motor motion

    turret.configOpenloopRamp(0.5); // 0.5 seconds from neutral to full output (during open-loop control)
    turret.configClosedloopRamp(0); // 0 disables ramping (during closed-loop control)
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
    SmartDashboard.putString("DB/String 0", "" + 
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0));
    SmartDashboard.putString("DB/String 1", "" + 
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0));
    SmartDashboard.putString("DB/String 2", "" + 
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0));
    SmartDashboard.putString("DB/String 3", "" + 
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0));
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
    // RightTrigger (axis-3) = clockwise, left trigger (axis-2) = counter-clockwise
    double stick = (xbox.getTriggerAxis(Hand.kRight) - xbox.getTriggerAxis(Hand.kLeft)) * -1; // -1 make forward stick positive
    System.out.println("stick:" + stick);

    // Use the left bumper (button 4) to do auto-tracking
    if (xbox.getBumper(Hand.kLeft)) {
      double heading_error = -tx.getDouble(0);  // -1 because either the motor or limelight is inverted.
      if (heading_error > 1.0)
      {
        stick = Kp*heading_error + min_command;
      }
      else if (heading_error < 1.0)
      {
        stick = Kp*heading_error - min_command;
      }
      // The Tv value from the lime-light returns 1 when the target
      // is in the field of view and returns 0 otherwise.
      stick = stick * tv.getDouble(0);  // stop rotating when target is out of sight
    }

    turret.set(ControlMode.PercentOutput, stick);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
