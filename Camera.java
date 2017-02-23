import com.github.sarxos.webcam.*;
import java.awt.Dimension;
import java.io.File;
import java.util.regex.Pattern;

public class Camera implements WebcamMotionListener {
    // vars n stuff
    Webcam webcam = null;
    WebcamMotionDetector detector = null;
    long lastEpoch = System.currentTimeMillis() / 1000L;
    String underscore = Pattern.quote("_");
    String dot = Pattern.quote(".");

    // config
    final File directory = new File(new File(System.getProperty("user.home")), "securitycam"); // dir to store pics
    final long hours = 48; // keep photos for 48 hours
    final long aftertime = 60 * 60 * 48; // start taking pictures again after 48 hours has passed

    public Camera() {
        // create directory for images if it doesn't exist
        if(!directory.exists())
            directory.mkdir();

        // initialize camera
        webcam = Webcam.getDefault();

        // set camera to max resolution
        Dimension[] res = webcam.getViewSizes();
        webcam.setViewSize(res[res.length - 1]);

        // open camera
        if(!webcam.isOpen())
            webcam.open();

        // set up motion detector
	detector = new WebcamMotionDetector(webcam);
	detector.setInterval(1000); // check every sec
	detector.addMotionListener(this);
        detector.start();
    }

    @Override
	public void motionDetected(WebcamMotionEvent wme) {
        // on motion sensed
        long currentEpoch = System.currentTimeMillis() / 1000L;

        if(currentEpoch - lastEpoch > aftertime) { // if it has been more than the set amount of time since last picture
            // then save the picture
            System.out.println("[" + new Date().toString() + "] Saving image...");
            WebcamUtils.capture(webcam, new File(directory, "capture_" + currentEpoch).getAbsolutePath(), "jpg");

            // check for old pics
            for(File f : directory.listFiles()) 
                if(f.isFile())
                    if(currentEpoch - Long.parseLong(f.getName().split(underscore)[1].split(dot)[0], 10) > hours * 60 * 60)
                        f.delete(); // delete old pics
        }

        // set latest timestamp
        lastEpoch = currentEpoch;
    }
}
