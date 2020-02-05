package studio.blacktech.coolqbot.furryblack.modules.Executor;


import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


@ModuleExecutorComponent
public class Executor_dice extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Dice";
	private static String MODULE_COMMANDNAME = "dice";
	private static String MODULE_DISPLAYNAME = "掷骰子";
	private static String MODULE_DESCRIPTION = "发送一个骰子";
	private static String MODULE_VERSION = "1.1";
	private static String[] MODULE_USAGE = new String[] {
			"/dice - 发送一个骰子", "/dice 理由 - 为某事投掷一枚骰子"
	};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================


	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================


	public Executor_dice() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}


	@Override
	public boolean init() throws Exception {
		ENABLE_USER = true;
		ENABLE_DISZ = true;
		ENABLE_GROP = true;
		return true;
	}


	@Override
	public boolean boot() throws Exception {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
		return true;
	}


	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"此模块无可用命令"
		};
	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}


	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {

		long userid = message.getUserID();

		// entry.userInfo(userid, message.getOptions() + "[CQ:dice]");
		entry.userInfo(userid, message.getCommandBody() + "[CQ:emoji,id=100000" + (new SecureRandom().nextInt(5) + 49) + "]");
		return true;

	}


	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {

		long diszid = message.getDiszID();
		long userid = message.getUserID();

		// entry.diszInfo(diszid, userid, message.getOptions() + "[CQ:dice]");
		entry.diszInfo(diszid, userid, message.getCommandBody() + "[CQ:emoji,id=100000" + (new SecureRandom().nextInt(5) + 49) + "]");
		return true;

	}


	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {

		long gropid = message.getGropID();
		long userid = message.getUserID();

		// entry.gropInfo(gropid, userid, message.getOptions() + "[CQ:dice]");
		entry.gropInfo(gropid, userid, message.getCommandBody() + "[CQ:emoji,id=100000" + (new SecureRandom().nextInt(5) + 49) + "]");
		return true;

	}


	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(Message message) {
		return new String[0];
	}

}
