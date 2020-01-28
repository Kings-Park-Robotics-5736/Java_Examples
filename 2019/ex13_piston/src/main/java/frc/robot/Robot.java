/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  //Timer timer = new Timer();
  private XboxController xbox = new XboxController(0);
  private DoubleSolenoid ballPusher = new DoubleSolenoid(0, 1);
  private DoubleSolenoid ballPincher = new DoubleSolenoid(2, 3);

  boolean isOpen = false;
  boolean pinchToggle = true;
  boolean pushToggle = true;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
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
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    // toggle logic from Mark_McLeod

    // TEST 1: test the ball pincher
    if (pinchToggle && xbox.getAButton() ) {  // Only execute once per Button push
      pinchToggle = false;     // Prevents this section of code from being called again until the Button is released and re-pressed
      if (isOpen) {
        isOpen = false;
        System.out.println("ball pincher open\n");
        ballPincher.set(DoubleSolenoid.Value.kForward);
      } else {
        isOpen = true;
        System.out.println("ball pincher close\n");
        ballPincher.set(DoubleSolenoid.Value.kReverse);
      }
    } else if(xbox.getAButton() == false) { 
      pinchToggle = true; // Button has been released, so this allows a re-press to activate the code above.
    }

    // TEST 2: test the ball pusher
    if (pushToggle && xbox.getBButton() ) {  // Only execute once per Button push
      pushToggle = false;     // Prevents this section of code from being called again until the Button is released and re-pressed
      if (isOpen) {
        isOpen = false;
        System.out.println("ball pusher open\n");
        ballPusher.set(DoubleSolenoid.Value.kForward);
      } else {
        isOpen = true;
        System.out.println("ball pusher close\n");
        ballPusher.set(DoubleSolenoid.Value.kReverse);
      }
    } else if(xbox.getBButton() == false) { 
      pushToggle = true; // Button has been released, so this allows a re-press to activate the code above.
    }

    // TEST 3: a single button should open the pincher and push the ball.
    if (xbox.getYButton()) {
      ballPincher.set(DoubleSolenoid.Value.kReverse); // open the pincher
      Timer.delay(1.0);  //in seconds
      // push the ball out of the ball pincher
      // the directio depends on the setup
      ballPusher.set(DoubleSolenoid.Value.kForward);  
      Timer.delay(1.0);
      ballPusher.set(DoubleSolenoid.Value.kReverse);  // retract the pusher
      // the 2 line below are not needed.  for testing purposes.
      // No need to close the pincher.
      Timer.delay(2.0);
      ballPincher.set(DoubleSolenoid.Value.kForward); // close the pincher
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
