package sound;
import java.io.File;
import java.util.Scanner;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * 音频录制
 * 
 * @author mars
 */
@SuppressWarnings("all")
public class SoundRecording {

	private static final long serialVersionUID = 1L;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;

	public SoundRecording() {
		System.out.println("y开始n结束");

		Scanner input = new Scanner(System.in);
		String Sinput = input.next();
		long testtime = System.currentTimeMillis();
		if (Sinput.equals("y")) {
			startRecording();// 调用录音方法
		}
		Scanner input_2 = new Scanner(System.in);
		String Sinput_2 = input_2.next();
		if (Sinput_2.equals("n")) {
			targetDataLine.stop();
			targetDataLine.close();
		}
		System.out.println("录音了" + (System.currentTimeMillis() - testtime) / 1000 + "秒！");
	}

	public void startRecording() {
		try {
			audioFormat = getAudioFormat();// 构造具有线性 PCM 编码和给定参数的 AudioFormat。
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			// 根据指定信息构造数据行的信息对象，这些信息包括单个音频格式。此构造方法通常由应用程序用于描述所需的行。
			// lineClass - 该信息对象所描述的数据行的类
			// format - 所需的格式
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			// 如果请求 DataLine，且 info 是 DataLine.Info 的实例（至少指定一种完全限定的音频格式），
			// 上一个数据行将用作返回的 DataLine 的默认格式。
			new CaptureThread().start();
			// 开启线程
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 8000F;
		// 8000,11025,16000,22050,44100 采样率
		int sampleSizeInBits = 16;
		// 8,16 每个样本中的位数
		int channels = 2;
		// 1,2 信道数（单声道为 1，立体声为 2，等等）
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false 指示是以 big-endian 顺序还是以 little-endian 顺序存储音频数据。
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);// 构造具有线性 PCM 编码和给定参数的
																							// AudioFormat。
	}

	class CaptureThread extends Thread {
		public void run() {
			AudioFileFormat.Type fileType = null;
			// 指定的文件类型
			File audioFile = null;
			// 设置文件类型和文件扩展名
			// 根据选择的单选按钮。
			fileType = AudioFileFormat.Type.WAVE;
			audioFile = new File("test.wav");
			try {
				targetDataLine.open(audioFormat);
				// format - 所需音频格式
				targetDataLine.start();
				// 当开始音频捕获或回放时，生成 START 事件。
				AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
				// new AudioInputStream(TargetDataLine
				// line):构造从指示的目标数据行读取数据的音频输入流。该流的格式与目标数据行的格式相同,line - 此流从中获得数据的目标数据行。
				// stream - 包含要写入文件的音频数据的音频输入流
				// fileType - 要写入的音频文件的种类
				// out - 应将文件数据写入其中的外部文件

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
