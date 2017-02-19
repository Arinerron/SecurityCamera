import com.github.sarxos.webcam.*;
import java.awt.Dimension;
import java.io.File;
import java.util.regex.Pattern;

public class Camera implements WebcamMotionListener {
    Webcam webcam = null;
    WebcamMotionDetector detector = null;
    File directory = new File(new File(System.getProperty("user.home")), "securitycam");
    long lastEpoch = System.currentTimeMillis() / 1000L;
    String underscore = Pattern.quote("_");
    String dot = Pattern.quote(".");

    final long hours = 48; // keep photos for 48 hours

    public Camera() {
        if(!directory.exists())
            directory.mkdir();

        webcam = Webcam.getDefault();
        Dimension[] res = webcam.getViewSizes();
        webcam.setViewSize(res[res.length - 1]);
        if(!webcam.isOpen())
            webcam.open();
		detector = new WebcamMotionDetector(webcam);
		detector.setInterval(1000); // check every sec
		detector.addMotionListener(this);
        detector.start();
    }

    @Override
	public void motionDetected(WebcamMotionEvent wme) {
        long currentEpoch = System.currentTimeMillis() / 1000L;

        if(currentEpoch - lastEpoch > 60) { // if it has been more than a minute since last picture
    		System.out.println("[" + currentEpoch + " || " + new Date().toString() + "] Saving image...");
            WebcamUtils.capture(webcam, new File(directory, "capture_" + currentEpoch).getAbsolutePath(), "jpg");

            for(File f : directory.listFiles()) {
                if(f.isFile()) {
                    if(currentEpoch - Long.parseLong(f.getName().split(underscore)[1].split(dot)[0], 10) > hours * 60 * 60)
                        f.delete();
                }
            }
        }

        lastEpoch = currentEpoch;
    }
}
