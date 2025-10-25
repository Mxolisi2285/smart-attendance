package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

@Service
public class FaceRecognitionService {

    private final LBPHFaceRecognizer recognizer;
    private final Map<Integer, Student> studentMap;

    public FaceRecognitionService() {
        recognizer = LBPHFaceRecognizer.create();
        studentMap = new HashMap<>();
    }

    // ================= TRAINING =================
    public void trainRecognizer(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students to train");
            return;
        }

        MatVector images = new MatVector(students.size());
        Mat labels = new Mat(students.size(), 1, CV_32SC1);

        int counter = 0;
        for (Student s : students) {
            byte[] faceBytes = s.getFaceEncoding();
            if (faceBytes == null || faceBytes.length == 0) {
                System.out.println("No face encoding for student: " + s.getName());
                continue;
            }

            try {
                // Try to decode as image
                Mat mat = new Mat(faceBytes);
                Mat decoded = org.bytedeco.opencv.global.opencv_imgcodecs.imdecode(mat, org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE);

                if (decoded != null && !decoded.empty()) {
                    images.put(counter, decoded);
                    labels.ptr(counter).putInt(counter);
                    studentMap.put(counter, s);
                    counter++;
                    System.out.println("Trained student: " + s.getName());
                } else {
                    System.out.println("Failed to decode image for student: " + s.getName());
                }

                // Clean up
                if (mat != null) mat.close();
                if (decoded != null) decoded.close();

            } catch (Exception e) {
                System.err.println("Error training student " + s.getName() + ": " + e.getMessage());
            }
        }

        if (counter > 0) {
            recognizer.train(images, labels);
            System.out.println("Training completed for " + counter + " students");
        } else {
            System.out.println("No valid training images found");
        }

        // Clean up
        images.close();
        labels.close();
    }

    // ================= FACE RECOGNITION =================
    public Student recognizeFace(Mat capturedFace) {
        if (capturedFace == null || capturedFace.empty()) {
            System.out.println("Captured face is null or empty");
            return null;
        }

        try {
            Mat gray = new Mat();
            cvtColor(capturedFace, gray, COLOR_BGR2GRAY);

            int[] label = new int[1];
            double[] confidence = new double[1];
            recognizer.predict(gray, label, confidence);

            System.out.println("Recognition result - Label: " + label[0] + ", Confidence: " + confidence[0]);

            // FIX: Lower confidence = better match
            if (confidence[0] < 80) { // Threshold can be adjusted
                Student recognized = studentMap.get(label[0]);
                if (recognized != null) {
                    System.out.println("Recognized: " + recognized.getName());
                }
                return recognized;
            } else {
                System.out.println("Confidence too low: " + confidence[0]);
            }

            gray.close();
        } catch (Exception e) {
            System.err.println("Error in face recognition: " + e.getMessage());
        }
        return null;
    }

    // ================= VIDEO PROCESSING =================
    public List<Student> extractFramesAndRecognize(MultipartFile videoFile) throws IOException {
        List<Student> recognizedStudents = new ArrayList<>();
        Set<Long> recognizedStudentIds = new HashSet<>();

        if (videoFile.isEmpty()) {
            throw new IOException("Uploaded video file is empty.");
        }

        System.out.println("Processing video file: " + videoFile.getOriginalFilename() + ", size: " + videoFile.getSize());

        FFmpegLogCallback.set();

        File tempFile = File.createTempFile("attendance_video_", ".webm");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(videoFile.getBytes());
        }

        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(tempFile);
            grabber.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame;
            int frameNumber = 0;
            int skipFrames = 10; // Process every 10th frame to reduce load

            System.out.println("Starting frame extraction...");

            while ((frame = grabber.grabImage()) != null) {
                if (frameNumber % skipFrames == 0) {
                    try {
                        Mat matFrame = convertFrameToMat(converter, frame);
                        if (!matFrame.empty()) {
                            Student recognized = recognizeFace(matFrame);
                            if (recognized != null && !recognizedStudentIds.contains(recognized.getId())) {
                                recognizedStudents.add(recognized);
                                recognizedStudentIds.add(recognized.getId());
                                System.out.println("Found student in video: " + recognized.getName());
                            }
                        }
                        matFrame.close();
                    } catch (Exception e) {
                        System.err.println("Error processing frame " + frameNumber + ": " + e.getMessage());
                    }
                }
                frameNumber++;
            }

            System.out.println("Processed " + frameNumber + " frames, recognized " + recognizedStudents.size() + " students");

        } catch (Exception e) {
            throw new IOException("Failed to process video: " + e.getMessage(), e);
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    System.err.println("Error closing grabber: " + e.getMessage());
                }
            }
            tempFile.delete();
        }

        return recognizedStudents;
    }

    // ================= UTILITY METHODS =================
    private Mat convertFrameToMat(Java2DFrameConverter converter, Frame frame) {
        try {
            BufferedImage bufferedImage = converter.getBufferedImage(frame);
            return bufferedImageToMat(bufferedImage);
        } catch (Exception e) {
            System.err.println("Error converting frame to Mat: " + e.getMessage());
            return new Mat();
        }
    }

    private Mat bufferedImageToMat(BufferedImage image) {
        if (image == null) return new Mat();

        try {
            Mat mat = new Mat(image.getHeight(), image.getWidth(), CV_8UC3);
            byte[] data = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
            mat.data().put(data);
            return mat;
        } catch (Exception e) {
            System.err.println("Error converting BufferedImage to Mat: " + e.getMessage());
            return new Mat();
        }
    }
}