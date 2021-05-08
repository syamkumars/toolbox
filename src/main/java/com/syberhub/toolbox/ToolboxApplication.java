package com.syberhub.toolbox;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.syberhub.toolbox.cowin.Cowin;

@SpringBootApplication()
public class ToolboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToolboxApplication.class, args);

		// Start timer
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					if (ToolBoxConfig.getBooleanProperty("job.enable.cowin.notification") == true) {
						System.out.println("Task performed on " + new Date());
						new Cowin().checkCowinSlot();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Timer timer = new Timer("Timer");

//		timer.scheduleAtFixedRate(task,  1000L,1000*60*60);//every hour
		timer.scheduleAtFixedRate(task,  1000L,1000*60);//every minute

	}

}
