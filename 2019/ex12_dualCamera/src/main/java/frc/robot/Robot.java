/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  UsbCamera camera0;
  UsbCamera camera1;
  private VideoSink server;
  private XboxController xbox;

  boolean whichCamera = false;
  boolean toggle = true;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    camera0 = CameraServer.getInstance().startAutomaticCapture(0);
    camera1 = CameraServer.getInstance().startAutomaticCapture(1);
    server = CameraServer.getInstance().getServer();
    xbox = new XboxController(0);
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
    if (toggle && xbox.getAButton() ) {  // Only execute once per Button push
      toggle = false;     // Prevents this section of code from being called again until the Button is released and re-pressed
      if (whichCamera) {  // Decide which way to set the camera this time through
        whichCamera= false;
        System.out.println("Setting camera 0\n");
        server.setSource(camera0);
      } else {
        whichCamera= true;
        System.out.println("Setting camera 1\n");
        server.setSource(camera1);
      }
    } else if(xbox.getAButton() == false) { 
        toggle = true; // Button has been released, so this allows a re-press to activate the code above.
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
