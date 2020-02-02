/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/**
 * Print out the various button presses from teh xbox controller.
 */

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// xbox specific files
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;


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

  private final XboxController xbox = new XboxController(0); // create xbox controller object


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
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

    // A-button (button 0)
    if (xbox.getAButton()) {
      System.out.println("xbox.getAButton()");
    }
    // A-button press
    if (xbox.getAButtonPressed()) {
      System.out.println("xbox.getAButtonPressed()");
    }
    // A-button release
    if (xbox.getAButtonReleased()) {
      System.out.println("xbox.getAButtonReleased()");
    }
    // B-button (button 1)
    if (xbox.getBButton()) {
      System.out.println("xbox.getBButton()");
    }
    // X-button (button 2)
    if (xbox.getXButton()) {
      System.out.println("xbox.getXButton()");
    }
    // Y-button (button 3)
    if (xbox.getYButton()) {
      System.out.println("xbox.getYButton()");
    }
    // left bumper (button 4)
    if (xbox.getBumper(Hand.kLeft)) {
      System.out.println("xbox.getBumper(hand.Kleft)");
    }
    // right bumper (button 5)
    if (xbox.getBumper(Hand.kRight)) {
      System.out.println("xbox.getBumper(Hand.kRight)");
    }
    // back button (button 6)
    if (xbox.getBackButton()) {
      System.out.println("xbox.getBackButton()");
    }
    // start button (button 7)
    if (xbox.getStartButton()) {
      System.out.println("xbox.getStartButton()");
    }
    // left stick - button 8
    if (xbox.getStickButton(Hand.kLeft)) {
      System.out.println("xbox.getStickButton(Hand.kLeft)");
    }
    // right stick - button 9
    if (xbox.getStickButton(Hand.kRight)) {
      System.out.println("xbox.getStickButton(Hand.kRight)");
    }    
    // Axis-0 (X), Axis-1 (Y)
    if (xbox.getStickButton(Hand.kRight)) {
      System.out.println("xbox.getX(Hand.kLeft) = " + xbox.getX(Hand.kLeft));
      System.out.println("xbox.getY(Hand.kLeft) = " + xbox.getY(Hand.kLeft));
    }
    // left trigger (axis-2)
    if (xbox.getTriggerAxis(Hand.kLeft) > 0) {
      System.out.println("xbox.getTriggerAxis(Hand.kLeft = )" + xbox.getTriggerAxis(Hand.kLeft));
    }
    // right trigger (axis-3)
    if (xbox.getTriggerAxis(Hand.kRight) > 0) {
      System.out.println("xbox.getTriggerAxis(Hand.kRight) = " + xbox.getTriggerAxis(Hand.kRight));
    }
    // Axis-4 (X) Axis-5 (Y)
    if (xbox.getRawButton(9)) {
      System.out.println("xbox.getRawButton(9) = " + xbox.getRawButton(9));
    }
    if (xbox.getStickButton(Hand.kLeft)) {
      System.out.println("xbox.getX(Hand.kRight) = " + xbox.getX(Hand.kRight));
      System.out.println("xbox.getY(Hand.kRight) = " + xbox.getY(Hand.kRight));
    }

    // POV in degrees -- North is 0 degrees
    if (xbox.getPOV() != -1) {
      System.out.println("xbox.getPOV() = " + xbox.getPOV());
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
