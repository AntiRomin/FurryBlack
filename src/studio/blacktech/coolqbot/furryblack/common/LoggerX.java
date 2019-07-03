package studio.blacktech.coolqbot.furryblack.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerX {

	private StringBuilder builder_mini;
	private StringBuilder builder_info;
	private StringBuilder builder_seek;
	private StringBuilder builder_full;

	public LoggerX() {
		this.builder_mini = new StringBuilder();
		this.builder_info = new StringBuilder();
		this.builder_seek = new StringBuilder();
		this.builder_full = new StringBuilder();
	}

	public String mini(String message) {
		this.builder_mini.append("[");
		this.builder_mini.append(time());
		this.builder_mini.append("] ");
		this.builder_mini.append(message);
		this.builder_mini.append("\r\n");
		return this.info(message);
	}

	public String info(String message) {
		this.builder_info.append("[");
		this.builder_info.append(time());
		this.builder_info.append("] ");
		this.builder_info.append(message);
		this.builder_info.append("\r\n");
		return this.seek(message);
	}

	public String seek(String message) {
		this.builder_seek.append("[");
		this.builder_seek.append(time());
		this.builder_seek.append("] ");
		this.builder_seek.append(message);
		this.builder_seek.append("\r\n");
		return this.full(message);
	}

	public String full(String message) {
		this.builder_full.append("[");
		this.builder_full.append(time());
		this.builder_full.append("] ");
		this.builder_full.append(message);
		this.builder_full.append("\r\n");
		return message;
	}

	public String mini(String name, String message) {
		this.builder_mini.append("[");
		this.builder_mini.append(time());
		this.builder_mini.append("] ");
		this.builder_mini.append(name);
		this.builder_mini.append(":");
		this.builder_mini.append(message);
		this.builder_mini.append("\r\n");
		return this.info(name, message);
	}

	public String info(String name, String message) {
		this.builder_info.append("[");
		this.builder_info.append(time());
		this.builder_info.append("] ");
		this.builder_info.append(name);
		this.builder_info.append(":");
		this.builder_info.append(message);
		this.builder_info.append("\r\n");
		return this.seek(name, message);
	}

	public String seek(String name, String message) {
		this.builder_seek.append("[");
		this.builder_seek.append(time());
		this.builder_seek.append("] ");
		this.builder_seek.append(name);
		this.builder_seek.append(":");
		this.builder_seek.append(message);
		this.builder_seek.append("\r\n");
		return this.full(name, message);
	}

	public String full(String name, String message) {
		this.builder_full.append("[");
		this.builder_full.append(time());
		this.builder_full.append("] ");
		this.builder_full.append(name);
		this.builder_full.append(":");
		this.builder_full.append(message);
		this.builder_full.append("\r\n");
		return message;
	}

	public long mini(long message) {
		this.builder_mini.append("[");
		this.builder_mini.append(time());
		this.builder_mini.append("] ");
		this.builder_mini.append(message);
		this.builder_mini.append("\r\n");
		return this.info(message);
	}

	public long info(long message) {
		this.builder_info.append("[");
		this.builder_info.append(time());
		this.builder_info.append("] ");
		this.builder_info.append(message);
		this.builder_info.append("\r\n");
		return this.seek(message);
	}

	public long seek(long message) {
		this.builder_seek.append("[");
		this.builder_seek.append(time());
		this.builder_seek.append("] ");
		this.builder_seek.append(message);
		this.builder_seek.append("\r\n");
		return this.full(message);
	}

	public long full(long message) {
		this.builder_full.append("[");
		this.builder_full.append(time());
		this.builder_full.append("] ");
		this.builder_full.append(message);
		this.builder_full.append("\r\n");
		return message;
	}

	public long mini(String name, long message) {
		this.builder_mini.append("[");
		this.builder_mini.append(time());
		this.builder_mini.append("] ");
		this.builder_mini.append(name);
		this.builder_mini.append(":");
		this.builder_mini.append(message);
		this.builder_mini.append("\r\n");
		return this.info(name, message);
	}

	public long info(String name, long message) {
		this.builder_info.append("[");
		this.builder_info.append(time());
		this.builder_info.append("] ");
		this.builder_info.append(name);
		this.builder_info.append(":");
		this.builder_info.append(message);
		this.builder_info.append("\r\n");
		return this.seek(name, message);
	}

	public long seek(String name, long message) {
		this.builder_seek.append("[");
		this.builder_seek.append(time());
		this.builder_seek.append("] ");
		this.builder_seek.append(name);
		this.builder_seek.append(":");
		this.builder_seek.append(message);
		this.builder_seek.append("\r\n");
		return this.full(name, message);
	}

	public long full(String name, long message) {
		this.builder_full.append("[");
		this.builder_full.append(time());
		this.builder_full.append("] ");
		this.builder_full.append(name);
		this.builder_full.append(":");
		this.builder_full.append(message);
		this.builder_full.append("\r\n");
		return message;
	}

	public String make(int level) {
		switch (level) {
		case 0:
			return this.builder_mini.toString();
		case 1:
			return this.builder_info.toString();
		case 2:
			return this.builder_seek.toString();
		default:
			return this.builder_full.toString();
		}
	}

	public static String time() {
		final SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
		return formater.format(new Date());
	}

	public static String datetime() {
		final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(new Date());
	}

	public static String datetime(final Date date) {
		final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date);
	}

	public static String time(final String formate) {
		final SimpleDateFormat formater = new SimpleDateFormat(formate);
		return formater.format(new Date());
	}

	public static String time(final String formate, final Date date) {
		final SimpleDateFormat formater = new SimpleDateFormat(formate);
		return formater.format(date);
	}

}
