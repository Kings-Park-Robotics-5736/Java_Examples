/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
//package org.usfirst.frc.team5736.robot;

import extraBits.*;

import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot implements PIDOutput {
  /* The following PID Controller coefficients will need to be tuned */
  /* to match the dynamics of your drive system.  Note that the      */
  /* SmartDashboard in Test mode has support for helping you tune    */
  /* controllers by displaying a form where you can enter new P, I,  */
  /* and D constants and test the mechanism.                         */
  static final double kP = 1;
  static final double kI = 0.00;
  static final double kD = 0.00;
  static final double kF = 0.00;

  /* This tuning parameter indicates how close to "on target" the    */
  /* PID Controller will attempt to get.                             */
  static final double kToleranceDegrees = 1.0;

  // create xbox controller object
  private XboxController xbox = new XboxController(0);

  // setup drive train
  private Talon leftMotor  = new Talon(0);
  private Talon rightMotor = new Talon(1);
  private DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor);

  // setup gyro
  private ADXRS450_Gyro gyro = new ADXRS450_Gyro();

  // setup pid contoller
  PIDController gryoController = new PIDController(kP, kI, kD, kF, gyro, this);
		//gryoController.setAbsoluteTolerance(1.0);
    //gryoController.setContinuous(true);
		//gryoController.setInputRange(-180.0, 180.0);
 // gryoController.setOutputRange(-0.5, 0.5);
    
//  double rotateToAngleRate;
  private Timer time = new Timer();

	public void turnForTime(double angle, double seconds)
	{
		gyro.reset();
		gryoController.enable();
		time.start();
		gryoController.setSetpoint(angle);
		while (time.get() < seconds);
		time.stop();
		time.reset();
		gryoController.disable();
		drive.stopMotor();
	}



  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    // drive train specific initialization
    drive.setSafetyEnabled(false);
    gyro.calibrate();

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

    // use right bumper to reset
    if (xbox.getBumperPressed(Hand.kRight)) {
      gyro.reset();
    }
/*
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
  */
    //boolean rotateToAngle = false;
    if ( xbox.getYButton() == true) {
      turnForTime(0.0f, 5);
        //pidController.setSetpoint(0.0f);
        //rotateToAngle = true;
    } else if (xbox.getBButton() == true) {
      turnForTime(90.0f, 5);
        //pidController.setSetpoint(90.0f);
        //rotateToAngle = true;
    } else if (xbox.getAButton()) {
      turnForTime(179.9f, 5);
//        pidController.setSetpoint(179.9f);
        //rotateToAngle = true;
    } else if (xbox.getXButton()) {
      turnForTime(-90.0f, 5);
        //pidController.setSetpoint(-90.0f);
        //rotateToAngle = true;
    }
    //double currentRotationRate;
    //if ( rotateToAngle ) {
        //pidController.enable();
        //currentRotationRate = rotateToAngleRate;
    //} else {
        //pidController.disable();
        //currentRotationRate = 0.0;
    //}
        /* Use the joystick X axis for lateral movement,          */
        /* Y axis for forward movement, and the current           */
        /* calculated rotation rate (or joystick Z axis),         */
        /* depending upon whether "rotate to angle" is active.    */
    //    myRobot.mecanumDrive_Cartesian(xbox.getX(), xbox.getY(), 
                                       //currentRotationRate, gyro.getAngle());

    //drive.arcadeDrive(0.5, gyro.getAngle());
//    Timer.delay(0.005);		// wait for a motor update time   
  }

  @Override
  /* This function is invoked periodically by the PID Controller, */
  /* based upon navX-MXP yaw angle input and PID Coefficients.    */
  public void pidWrite(double output) {
    leftMotor.set(output);
    rightMotor.set(output);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
