package org.usfirst.frc.team1155.robot.commands;

import org.usfirst.frc.team1155.robot.Hardware;
import org.usfirst.frc.team1155.robot.Robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class SwerveDriveCommand extends Command{
	
	CANTalon FLdrive, FLsteer, RLdrive, RLsteer, FRdrive, FRsteer, RRdrive, RRsteer;
	Joystick rightJoy, leftJoy;
	AnalogGyro gyro;

	double forwardRaw;
	double strafeRaw;
	double rotateClockwiseRaw;
	double temp;
	double forwardFieldCentric;
	double strafeFieldCentric;
	double wheelbase;
	double trackwidth;
	double diagonal;
	double a;
	double b;
	double c;
	double d;
	double frontRSpeed, frontLSpeed, backLSpeed, backRSpeed; //Front Right, Front Left, Rear Left, Rear Right Wheel Speeds, respectively
	double frontRAngle, frontLAngle, backLAngle, backRAngle; //Wheel Angles
	double max;
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		requires(Robot.drive);
		FLdrive = Hardware.INSTANCE.driveFrontL;
		FLsteer = Hardware.INSTANCE.steerFrontL;
		RLdrive = Hardware.INSTANCE.driveBackL;
		RLsteer = Hardware.INSTANCE.steerBackL;
		FRdrive = Hardware.INSTANCE.driveFrontR;
		FRsteer = Hardware.INSTANCE.steerFrontR;
		RRdrive = Hardware.INSTANCE.driveBackR;
		RRsteer = Hardware.INSTANCE.steerBackR;
		gyro = Hardware.INSTANCE.gyro;
		rightJoy = Hardware.INSTANCE.rightJoy;
		leftJoy = Hardware.INSTANCE.leftJoy;
	}

	@Override
	protected void execute() {
		forwardRaw = (leftJoy.getY() + rightJoy.getY())/2;
     	strafeRaw = (leftJoy.getX() + rightJoy.getX())/2;
     	rotateClockwiseRaw = leftJoy.getZ();
     	
     	temp = (forwardRaw * Math.cos(gyro.getAngle())) + strafeRaw * Math.sin(gyro.getAngle());
     	strafeFieldCentric = (-forwardRaw * Math.sin(gyro.getAngle())) + strafeRaw * Math.cos(gyro.getAngle());
     	forwardFieldCentric = temp;
     	
    	wheelbase = 30; //length of drivebase
   	trackwidth = 24; //width of drivebase
     	diagonal = Math.sqrt((wheelbase*wheelbase) + (trackwidth*trackwidth));
     	
     	a = strafeFieldCentric - rotateClockwiseRaw * (wheelbase/diagonal);
     	b = strafeFieldCentric + rotateClockwiseRaw * (wheelbase/diagonal);
     	c = forwardFieldCentric - rotateClockwiseRaw * (trackwidth/diagonal);
     	d = forwardFieldCentric + rotateClockwiseRaw * (trackwidth/diagonal);
     	
     	frontRSpeed = Math.sqrt(b*b + c*c);
     	frontLSpeed = Math.sqrt(b*b + d*d);
     	backLSpeed = Math.sqrt(a*a + d*d);
     	backRSpeed = Math.sqrt(a*a + c*c);
     	frontRAngle = Math.atan2(b,c) * 180/Math.PI;
     	frontLAngle = Math.atan2(b,d) * 180/Math.PI;
     	backRAngle = Math.atan2(a,d) * 180/Math.PI;
     	backLAngle = Math.atan2(a,c) * 180/Math.PI;
     	
     	//Because the CANTalon position control uses values from 0 - 1023 
     	//for potentiometer ranges, we must modify the wheel angles to 
     	//compenstate for this. To do this add 180 to the wheel angle to 
     	//get a 0-360 range then multiply by 1023/360 which equals 2.8444...
     	Robot.drive.setAngle((frontRAngle+180) * (1023/360),
     				   (frontLAngle+180) * (1023/360),
     				   (backLAngle+180) * (1023/360), 
     				   (backRAngle+180) * (1023/360));
     	
     	//Normalize wheel speeds as stated in Ether's document
     	max = frontRSpeed;
     	if(frontLSpeed>max){
     		max = frontLSpeed;
     	}
     	if(backLSpeed>max){
     		max = backLSpeed;
     	}
     	if(backRSpeed>max){
     		max = backRSpeed;
     	}
     	if(max>1){
     		frontRSpeed/=max;
     		frontLSpeed/=max;
     		backRSpeed/=max;
     		backLSpeed/=max;
     	}
     	
     	Robot.drive.setSpeed(frontRSpeed, frontLSpeed, backRSpeed, backLSpeed);
  				  		
  	}		  	
  		  
		
	

	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interrupted() {
		// TODO Auto-generated method stub
		
	}

}
