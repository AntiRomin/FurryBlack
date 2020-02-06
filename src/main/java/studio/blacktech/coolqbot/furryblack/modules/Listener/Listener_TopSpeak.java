package studio.blacktech.coolqbot.furryblack.modules.Listener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.meowy.cqp.jcq.entity.CQImage;
import org.meowy.cqp.jcq.message.CQCode;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleListenerComponent;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;


@ModuleListenerComponent
public class Listener_TopSpeak extends ModuleListener {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Listener_TopSpeak";
	private static String MODULE_COMMANDNAME = "shui";
	private static String MODULE_DISPLAYNAME = "水群分析";
	private static String MODULE_DESCRIPTION = "水群分析";
	private static String MODULE_VERSION = "2.0.0";
	private static String[] MODULE_USAGE = new String[] {
			"重启线程 重连数据库 /admin exec --module=shui reload",
			"执行语句/admin exec --module=shui execute --explain `SQL`",
			"确认执行 /admin exec --module=shui execute --analyze `SQL`",
			"取消执行 /admin exec --module=shui decline",
			"执行命令 浮动结果集 /admin exec --module=shui confirm",
			"执行命令 单向结果集 /admin exec --module=shui confirm --no-size",
			"保存结果集 /admin exec --module=shui save",
			"显示结果集 /admin exec --module=shui show",
			"关闭结果集 /admin exec --module=shui done"
	};
	private static String[] MODULE_PRIVACY_STORED = new String[] {
			"按照\"群-成员-消息\"的层级关系保存所有聊天内容"
	};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private File CONFIG_ENABLE_RECORD;
	private File CONFIG_ENABLE_REPORT;

	private ArrayList<Long> GROUP_RECORD;
	private ArrayList<Long> GROUP_REPORT;

	private Thread thread;

	private boolean JDBC_ENABLE;
	private String JDBC_HOSTNAME;
	private String JDBC_USERNAME;
	private String JDBC_PASSWORD;

	private Object lock;
	private BlockingQueue<MessageGrop> queue;

	private Connection connection;
	private String pendingCommand;
	private ResultSet lastResult;
	private int lastColSize;
	private int lastRowSize;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Listener_TopSpeak() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();
		initLogsFolder();
		initDataFolder();
		initPropertiesConfigurtion();

		lock = new Object();
		queue = new LinkedBlockingQueue<>();

		if (NEW_CONFIG) {
			logger.seek("配置文件不存在 - 生成默认配置");
			CONFIG.setProperty("postgresql.enable", "false");
			CONFIG.setProperty("postgresql.hostname", "jdbc:postgresql://localhost:5432/furryblack");
			CONFIG.setProperty("postgresql.username", "furryblack");
			CONFIG.setProperty("postgresql.password", "furryblack");
			saveConfig();
		} else {
			loadConfig();
		}

		JDBC_ENABLE = Boolean.parseBoolean(CONFIG.getProperty("postgresql.enable"));

		if (!JDBC_ENABLE) {
			ENABLE_USER = false;
			ENABLE_DISZ = false;
			ENABLE_GROP = false;
			return true;
		}

		JDBC_HOSTNAME = CONFIG.getProperty("postgresql.hostname");
		JDBC_USERNAME = CONFIG.getProperty("postgresql.username");
		JDBC_PASSWORD = CONFIG.getProperty("postgresql.password");

		logger.seek("数据库", JDBC_HOSTNAME);
		logger.seek("帐号", JDBC_USERNAME);
		logger.seek("密码", JDBC_PASSWORD);

		// =================================================================
		// 测试数据库

		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);
		if (connection == null) throw new InitializationException("数据库连接失败");

		// =================================================================
		// 初始化内存

		GROUP_RECORD = new ArrayList<>();
		GROUP_REPORT = new ArrayList<>();

		CONFIG_ENABLE_RECORD = Paths.get(FOLDER_CONF.getAbsolutePath(), "enable_record.txt").toFile();
		CONFIG_ENABLE_REPORT = Paths.get(FOLDER_CONF.getAbsolutePath(), "enable_report.txt").toFile();

		if (!CONFIG_ENABLE_RECORD.exists() && !CONFIG_ENABLE_RECORD.createNewFile()) throw new InitializationException("无法创建文件" + CONFIG_ENABLE_RECORD.getName());
		if (!CONFIG_ENABLE_REPORT.exists() && !CONFIG_ENABLE_REPORT.createNewFile()) throw new InitializationException("无法创建文件" + CONFIG_ENABLE_REPORT.getName());

		// =================================================================
		// 读取配置文件

		String line;

		BufferedReader recordReader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_RECORD), StandardCharsets.UTF_8));
		BufferedReader reportReader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_REPORT), StandardCharsets.UTF_8));

		while ((line = recordReader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
			GROUP_RECORD.add(Long.parseLong(line));
			logger.seek("记录群聊", line);
		}

		recordReader.close();


		while ((line = reportReader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
			GROUP_REPORT.add(Long.parseLong(line));
			logger.seek("每日汇报", line);
		}

		reportReader.close();

		ENABLE_USER = false;
		ENABLE_DISZ = false;
		ENABLE_GROP = true;

		return true;
	}


	@Override
	public boolean boot() throws Exception {
		logger.info("启动工作线程");
		thread = new Thread(new Worker(queue, lock, connection));
		thread.start();
		return true;
	}


	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
		logger.info("关闭工作线程");
		thread.interrupt();
		thread.join();
		logger.info("关闭数据库连接");
		connection.close();
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {

		if (message.getParameterSection() < 1) return MODULE_USAGE;

		int count = 0;
		int limit = Integer.MAX_VALUE; // 0x7FFFFFFF -> 我有理由相信java是小端型

		switch (message.getParameterSegment(1)) {

		case "reload":
			thread.interrupt();
			thread.join();
			connection.close();
			connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);
			thread = new Thread(new Worker(queue, lock, connection));
			thread.start();
			return new String[] {
					"工作线程与数据库连接重启完成"
			};


		case "execute":
			if (message.getParameterSection() < 3) return new String[] {
					"execute: 需要语句"
			};
			boolean explain = message.hasSwitch("explain");
			boolean analyze = message.hasSwitch("analyze");
			String prefix = null;
			if (explain) {
				prefix = "EXPLAIN ";
			} else if (analyze) {
				prefix = "EXPLAIN ANALYZE ";
			}
			pendingCommand = (prefix == null ? "" : prefix) + message.getParameterSegment(2);
			return new String[] {
					"命令已暂存\r\n" + pendingCommand + "\r\n/admin exec --module=shui confirm\r\n/admin exec --module=shui decline"
			};


		case "confirm":
			if (pendingCommand == null) return new String[] {
					"暂存区没有命令"
			};

			if (message.hasSwitch("no-size")) {
				Statement statement = connection.createStatement();
				boolean result = statement.execute(pendingCommand);
				return new String[] {
						"命令执行" + (result ? "成功" : "失败") + "\r\n/admin exec --module=shui save\r\n/admin exec --module=shui show"
				};
			} else {
				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				boolean result = statement.execute(pendingCommand);
				lastResult = statement.getResultSet();
				lastResult.last();
				lastColSize = lastResult.getMetaData().getColumnCount();
				lastRowSize = lastResult.getRow();
				lastResult.beforeFirst();
				return new String[] {
						"命令执行" + (result ? "成功" : "失败") + "\r\n结果集 " + lastColSize + "列 × " + lastRowSize + "行\r\n共" + lastColSize * lastRowSize + "项\r\n/admin exec --module=shui save\r\n/admin exec --module=shui show"
				};
			}


		case "decline":
			pendingCommand = null;
			return new String[] {
					"暂存区命令已清空"
			};


		case "show":
			if (message.hasSwitch("limit")) limit = Integer.parseInt(message.getSwitch("limit"));
			StringBuilder builder = new StringBuilder();
			while (lastResult.next() && count++ < limit) {
				builder.append("# Row - " + count);
				for (int i = 1; i <= lastColSize; i++) {
					builder.append("Col " + i + "=");
					builder.append(lastResult.getString(i));
					builder.append("\r\n");
				}
			}

			builder.setLength(builder.length() - 2);
			return new String[] {
					builder.toString()
			};


		case "done":
			pendingCommand = null;
			lastResult.close();
			return new String[] {
					"结果集已关闭"
			};


		case "save":
			File file = Paths.get(FOLDER_LOGS.getAbsolutePath(), "SQL_result_" + System.currentTimeMillis() + ".csv").toFile();

			FileWriter writer = new FileWriter(file);

			while (lastResult.next()) {
				for (int i = 1; i <= lastColSize; i++) {
					writer.append("\"");
					writer.append(lastResult.getString(i).replaceAll("\"", "\"\""));
					writer.append("\",");
				}
				writer.append("\n");
			}
			writer.flush();
			writer.close();
			lastResult.close();
			return new String[] {
					"结果集已保存 " + file.getAbsolutePath()
			};
		}

		return new String[] {
				"无可用消息"
		};
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {
		return false;
	}


	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {
		return false;
	}


	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {
		if (GROUP_RECORD.contains(message.getGropID())) {
			queue.put(message);
			if (queue.size() > 10) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		}
		return true;

	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	@Override
	public String[] generateReport(Message message) {
		return new String[] {
				"无可用消息"
		};
	}


	class Worker implements Runnable {

		private Object lock;

		private BlockingQueue<MessageGrop> queue;

		private Connection connection;

		private PreparedStatement chat_record_Statement;
		private PreparedStatement record_at_Statement;
		private PreparedStatement record_rps_Statement;
		private PreparedStatement record_dice_Statement;
		private PreparedStatement record_image_Statement;
		private PreparedStatement record_face_Statement;
		private PreparedStatement record_sface_Statement;
		private PreparedStatement record_bface_Statement;
		private PreparedStatement record_emoji_Statement;


		public Worker(BlockingQueue<MessageGrop> queue, Object queueLock, Connection connection) {
			this.queue = queue;
			lock = queueLock;
			this.connection = connection;
		}


		@Override
		public void run() {

			do {

				try {

					chat_record_Statement = connection.prepareStatement("INSERT INTO chat_record VALUES (?,?,?,?,?,?,?,?)");
					record_at_Statement = connection.prepareStatement("INSERT INTO record_at VALUES (?,?)");
					record_rps_Statement = connection.prepareStatement("INSERT INTO record_rps VALUES (?,?)");
					record_dice_Statement = connection.prepareStatement("INSERT INTO record_dice VALUES (?,?)");
					record_face_Statement = connection.prepareStatement("INSERT INTO record_face VALUES (?,?)");
					record_emoji_Statement = connection.prepareStatement("INSERT INTO record_emoji VALUES (?,?)");
					record_sface_Statement = connection.prepareStatement("INSERT INTO record_sface VALUES (?,?)");
					record_bface_Statement = connection.prepareStatement("INSERT INTO record_bface VALUES (?,?)");
					record_image_Statement = connection.prepareStatement("INSERT INTO record_image VALUES (?,?,?)");

					while (true) {
						synchronized (lock) {
							lock.wait(5000);
						}
						flush();
					}

				} catch (Exception exception) {
					if (entry.isEnable()) {
						entry.adminInfo("写入线程发生异常 " + exception.toString());
						logger.exception(exception);
						exception.printStackTrace();
					} else {
						logger.full("写入线程关闭");
						try {
							flush();
						} catch (Exception ignored) {

						}
					}
				}

			} while (entry.isEnable());
			logger.full("工作线程结束");
		}


		private void flush() throws InterruptedException, SQLException, IOException {

			while (queue.size() > 0) {

				MessageGrop message = queue.take();

				long message_id = message.getMessageID();

				chat_record_Statement.setLong(1, message_id);
				chat_record_Statement.setLong(2, message.getMessageFont());
				chat_record_Statement.setTimestamp(3, new Timestamp(message.getSendtime()));
				chat_record_Statement.setLong(4, message.getGropID());
				chat_record_Statement.setLong(5, message.getUserID());
				chat_record_Statement.setInt(6, message.getType().getID());
				chat_record_Statement.setString(7, message.getMessage());
				chat_record_Statement.setString(8, message.getContent());

				chat_record_Statement.execute();

				if (message.hasAt()) {
					for (String temp : message.getAt()) {
						record_at_Statement.setLong(1, message_id);
						record_at_Statement.setLong(2, Long.parseLong(temp));
						record_at_Statement.execute();
					}
				}

				if (message.hasRps()) {
					for (String temp : message.getRps()) {
						record_rps_Statement.setLong(1, message_id);
						record_rps_Statement.setLong(2, Long.parseLong(temp));
						record_rps_Statement.execute();
					}
				}

				if (message.hasDice()) {
					for (String temp : message.getDice()) {
						record_dice_Statement.setLong(1, message_id);
						record_dice_Statement.setLong(2, Long.parseLong(temp));
						record_dice_Statement.execute();
					}
				}

				if (message.hasEmoji()) {
					for (String temp : message.getEmoji()) {
						record_emoji_Statement.setLong(1, message_id);
						record_emoji_Statement.setLong(2, Long.parseLong(temp));
						record_emoji_Statement.execute();
					}
				}

				if (message.hasFace()) {
					for (String temp : message.getFace()) {
						record_face_Statement.setLong(1, message_id);
						record_face_Statement.setLong(2, Long.parseLong(temp));
						record_face_Statement.execute();
					}
				}

				if (message.hasSface()) {
					for (String temp : message.getSface()) {
						record_sface_Statement.setLong(1, message_id);
						record_sface_Statement.setString(2, temp);
						record_sface_Statement.execute();
					}
				}

				if (message.hasBface()) {
					for (String temp : message.getBface()) {
						record_bface_Statement.setLong(1, message_id);
						record_bface_Statement.setString(2, temp);
						record_bface_Statement.execute();
					}
				}

				if (message.hasImage()) {

					for (String temp : message.getImage()) {

						CQImage image = CQCode.getInstance().getCQImage(temp);

						// Jcq BUG -> CQCode.getInstance().getCQImage() 使用的是已废弃的CQImage构造方法 getName()会返回null
						// String filename = image.getName();

						String filename = temp.substring(15, 51);
						// entry.getCQ().getImage(file);

						File file = Paths.get(entry.getPictureStorePath(), filename).toFile();

						if (!file.exists()) image.download(file);

						record_image_Statement.setLong(1, message_id);
						record_image_Statement.setString(2, temp);
						record_image_Statement.setString(3, image.getUrl());
						record_image_Statement.execute();
					}
				}
			}
		}
	}
}
