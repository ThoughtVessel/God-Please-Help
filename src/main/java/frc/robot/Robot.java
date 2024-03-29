// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import frc.robot.Constants;

import java.lang.Thread;
import java.lang.Math;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {

  /////////////////
  //Drivetrain Stuff
  //////////////////
  private CANSparkMax leftFrontMotor = new CANSparkMax(Constants.LEFT_FRONT_MOTOR_ID, MotorType.kBrushed);
  private CANSparkMax leftBackMotor = new CANSparkMax(Constants.LEFT_BACK_MOTOR_ID, MotorType.kBrushed);
  private CANSparkMax rightFrontMotor = new CANSparkMax(Constants.RIGHT_FRONT_MOTOR_ID, MotorType.kBrushed);
  private CANSparkMax rightBackMotor = new CANSparkMax(Constants.RIGHT_BACK_MOTOR_ID, MotorType.kBrushed);

  private MotorControllerGroup leftMotorGroup = new MotorControllerGroup(leftFrontMotor, leftBackMotor);
  private MotorControllerGroup rightMotorGroup = new MotorControllerGroup(rightFrontMotor, rightBackMotor);

  private DifferentialDrive difDrive = new DifferentialDrive(rightMotorGroup, leftMotorGroup);

  //private final PWMSparkMax m_leftDrive = new PWMSparkMax(0);
  //private final PWMSparkMax m_rightDrive = new PWMSparkMax(1);
  //private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftDrive, m_rightDrive);
  
  private final Joystick joystick = new Joystick(0);
  private final XboxController controller = new XboxController(0);
  //Button intakeLower = new JoystickButton(joystick, 3);
  //Button intakeHigher = new JoystickButton(joystick, 4);
  

  private final Timer m_timer = new Timer();

  //////////////
  //Intake stuff
  //////////////
  private boolean toggleIntake = false;
  private double intakeSpeed = Constants.INTAKE_MOTOR_SPEED_LOW;

  private CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_ID, MotorType.kBrushless);
  


  //////////////////
  //Shooter Stuff
  //////////////////
  private Spark shooterMotor = new Spark(Constants.SHOOTER_MOTOR_ID);
  private double startShootingTime = 1000.0;

  //////////////////
  //Intake Stuff Stuff  FB
  //////////////////
  private boolean toggleTransition = false;
  private Spark transitionMotor = new Spark(Constants.TRANSITION_MOTOR_ID);
  




  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    rightMotorGroup.setInverted(true);
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // Spin 360 degrees to deploy intake
    // Reverse shooter to deploy ball into transition
    // Spin up shooter
    // Turn Transition
    // Back up
    /*
    if (m_timer.get() < 2.0) {
      // Driv backward for space (1s)
      difDrive.arcadeDrive(0.5, 0);

    } else if (m_timer.get() < 2.5) {
      // Drive forward
      difDrive.arcadeDrive(-1,0);

    } else if (m_timer.get() < 3.0) {
      // Drive backward to deploy intake
      difDrive.arcadeDrive(0.6, 0);

    } else if (m_timer.get() < 3.5) {
      // Drive forward to reposition a little
      difDrive.arcadeDrive(0.0,0);
    } else if (m_timer.get() < 7.0) {
      // Stop driving
      difDrive.arcadeDrive(0,0);
      // Reverse shooter to deploy ball into transition (0.5s)
      shooterMotor.set(-0.4);
      transitionMotor.set(-0.4);
    } else if (m_timer.get() < 9) {
      shooterMotor.set(1);
    } else if (m_timer.get() < 10){
      transitionMotor.set(1);
    } else if (m_timer.get() < 13){
      difDrive.arcadeDrive(0.5, 0);
      transitionMotor.stopMotor();
      shooterMotor.stopMotor();
    } else {
      // Stop everything
      difDrive.stopMotor();

    }
    */
  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    startShootingTime = -100.0;

    m_timer.reset();
    m_timer.start();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //difDrive.tankDrive(rightJoystick.getY(), leftJoystick.getY());
    difDrive.arcadeDrive(controller.getLeftY(), controller.getRightX()*0.4 + (controller.getRightX()/(Math.abs(controller.getRightX())))*0.3);

    //Allows the intake to be activated.
    intakeStuff();

    //Shooting stuff
    shootStuff();


    //Button if statements
    buttonIfStatements();
    



    //Control the transition/shooter.
    //shooterMotor.set((joystick.getThrottle() / 2) + 0.5);
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}


  ///////////////
  //Functions
  //////////////
  
  //Runs all the button if statements.
  private void buttonIfStatements(){
    /*
    if(controller.getRawButtonPressed(3)){
      intakeSpeed -= 0.05;
    }
    if(controller.getRawButtonPressed(4)){
      intakeSpeed += 0.05;
    }
    */
    if(controller.getRawButtonPressed(10)){
      intakeSpeed *= -1;
    }
  }

  //Intake Stuff Function
  private void intakeStuff(){
    ///Toggles intake from on to off fast speed
    if (controller.getRawButtonPressed(5)) {
      if (toggleIntake) {
        // Current state is true so turn off
        toggleIntake = false;
      } else {
        // Current state is false so turn on
        toggleIntake = true;
        intakeSpeed = 0.6;
      }
    }

    //Slow speed intake
    if (controller.getRawButtonPressed(6)) {
      if (toggleIntake) {
        // Current state is true so turn off
        toggleIntake = false;
      } else {
        // Current state is false so turn on
        toggleIntake = true;
        intakeSpeed = Constants.INTAKE_MOTOR_SPEED_LOW;
      }
    }

    /*
    if(controller.getRawButtonPressed(3)){
      if(intakeSpeed < 1){
        intakeSpeed += 0.1;
      }
    }
    if(controller.getRawButtonPressed(4)){
      if(intakeSpeed > -1){
        intakeSpeed -= 0.1;
      }
      
    }
    */

    if(toggleIntake){
      //ToDo set intake motor to on
      intakeMotor.set(intakeSpeed);
      transitionMotor.set(1);
    } else {
      //To do set intake motor to off
      intakeMotor.stopMotor();
      transitionMotor.stopMotor();
    }
  }

  //Transition Function (not used currently)
  private void transitionStuff(){
    ///Toggles transition from on to off
    if (controller.getRawButtonPressed(6)) {
      if (toggleTransition) {
        // Current state is true so turn off
        toggleTransition = false;
      } else {
        // Current state is false so turn on
        toggleTransition = true;
      }
    }

    if(toggleTransition){
      //ToDo set intake motor to on
      transitionMotor.set(Constants.TRANSITION_MOTOR_SPEED);
    } else {
      //To do set intake motor to off
      transitionMotor.stopMotor();
    }
  }

  //Shooter function
  private void shootStuff(){
    boolean toggleShooter = true;
    //If person clicks the button, reset the startShooting timer.
    if(m_timer.get() - startShootingTime > 2.5){
      if(controller.getRawButtonPressed(4)){
        startShootingTime = m_timer.get();
        toggleShooter = true;
      }
    } else {
      toggleShooter = true;
    }

    if(m_timer.get() - startShootingTime > 2.8){
      toggleShooter = false;
    }

    if(toggleShooter){
      //Called routinely. If the shooting time has been reset, the thiong goews through its process.
      if(m_timer.get() - startShootingTime < 2){
        shooterMotor.set(Constants.SHOOTER_MOTOR_SPEED);
      } else if(m_timer.get() - startShootingTime < 2.5){
        shooterMotor.set(-0.2);
      } else {
        shooterMotor.stopMotor();
      } 
      if(m_timer.get() - startShootingTime < 1){
        transitionMotor.set(-0.4);
      } else if( m_timer.get() - startShootingTime < 2){
        transitionMotor.set(1);
      } else {
        transitionMotor.stopMotor();
      }
    }

  }

}
