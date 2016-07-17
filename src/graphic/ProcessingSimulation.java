package graphic;

import processing.core.*;

public class ProcessingSimulation extends PApplet{
	
	public void settings() {
		size(600,600);
	}
	
	float x = 100;
	float y = 100;
	float xspeed = 1;
	float yspeed = (float)3.3;

	public void setup() {
	  smooth();
	  background(255);
	}

	public void draw() {
	  noStroke();
	  fill(255,10);
	  rect(0,0,width,height);
	  
	  // Add the current speed to the location.
	  x = x + xspeed;
	  y = y + yspeed;

	  // Check for bouncing
	  if ((x > width) || (x < 0)) {
	    xspeed = xspeed * -1;
	  }
	  if ((y > height) || (y < 0)) {
	    yspeed = yspeed * -1;
	  }

	  // Display at x,y location
	  stroke(0);
	  fill(175);
	  ellipse(x,y,16,16);
	}
	
}
