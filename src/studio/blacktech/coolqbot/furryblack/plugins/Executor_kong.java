package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

@SuppressWarnings("unused")
public class Executor_kong extends ModuleExecutor {

	private String MODULE_DISPLAYNAME = "���";
	private String MODULE_PACKAGENAME = "kong";
	private String MODULE_DESCRIPTION = "�� �� �� �� �� ��";
	private String MODULE_VERSION = "2.0.0";
	private String[] MODULE_USAGE = {
			"//kong ��Ҫ�����ԭ��"
	};
	private String[] MODULE_PRIVACY_LISTEN = {};
	private String[] MODULE_PRIVACY_EVENTS = {};
	private String[] MODULE_PRIVACY_STORED = {};
	private String[] MODULE_PRIVACY_CACHED = {};
	private String[] MODULE_PRIVACY_OBTAIN = {
			"��ȡ�������"
	};

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.userInfo(userid, message.length == 1 ? "�� �� �� �� �� �� �� ��" : Executor_kong.kong(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, message.length == 1 ? "�� �� �� �� �� �� �� ��" : Executor_kong.kong(message));
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, message.length == 1 ? "�� �� �� �� �� �� �� ��" : Executor_kong.kong(message));
		return true;
	}

	private static String kong(Message message) {
		String temp;
		temp = message.join(1);
		temp = temp.replaceAll(" ", "");
		temp = temp.trim();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < temp.length(); i++) {
			builder.append(temp.charAt(i));
			builder.append(" ");
		}
		builder.setLength(builder.length() - 1);
		return builder.toString();
	}

	@Override
	public String getReport() {
		return null;
	}

}