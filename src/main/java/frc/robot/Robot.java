// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import frc.robot.Constants;

import java.lang.Thread;

import edu.wpi.first.wpilibj.Joystick;
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
  //Button intakeLower = new JoystickButton(joystick, 3);
  //Button intakeHigher = new JoystickButton(joystick, 4);
  

  private final Timer m_timer = new Timer();

  //////////////
  //Intake stuff
  //////////////
  private boolean toggleIntake = false;
  private double intakeSpeed = Constants.INTAKE_MOTOR_SPEED;

  private CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_ID, MotorType.kBrushless);
  


  //////////////////
  //Shooter Stuff
  //////////////////
  private Spark shooterMotor = new Spark(Constants.SHOOTER_MOTOR_ID);

  //////////////////
  //Intake Stuff Stuff
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
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      difDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      difDrive.stopMotor(); // stop robot
    }
  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //difDrive.tankDrive(rightJoystick.getY(), leftJoystick.getY());
    difDrive.arcadeDrive(joystick.getY(), joystick.getX());


    intakeStuff();


    shootStuff();


    //Button if statements
    buttonIfStatements();
    



    //Control the transition/shooter.
    shooterMotor.set((joystick.getThrottle() / 2) + 0.5);
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
    if(joystick.getRawButtonPressed(3)){
      intakeSpeed -= 0.05;
    }
    if(joystick.getRawButtonPressed(4)){
      intakeSpeed += 0.05;
    }
    if(joystick.getRawButtonPressed(5)){
      intakeSpeed *= -1;
    }
  }

  //Intake stuff function
  private void intakeStuff(){
    ///Toggles intake from on to off
    if (joystick.getRawButtonPressed(2)) {
      if (toggleIntake) {
        // Current state is true so turn off
        toggleIntake = false;
      } else {
        // Current state is false so turn on
        toggleIntake = true;
        intakeSpeed = Constants.INTAKE_MOTOR_SPEED;
      }
    }

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

  //Transition Function
  private void transitionStuff(){
    ///Toggles transition from on to off
    if (joystick.getRawButtonPressed(6)) {
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
      transitionMotor.set(1);
    } else {
      //To do set intake motor to off
      transitionMotor.stopMotor();
    }
  }

  //Shooter function
  private void shootStuff(){
    if(joystick.getTriggerPressed()){
      shooterMotor.set(-1);
      Timer.delay(2);
      transitionMotor.set(1);
      Timer.delay(1);
      shooterMotor.stopMotor();
      transitionMotor.stopMotor();
    }
  }

}
