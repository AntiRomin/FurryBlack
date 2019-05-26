package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_chou extends ModuleExecutor {

	public Executor_chou() {
		this.MODULE_DISPLAYNAME = "随机抽人";
		this.MODULE_PACKAGENAME = "chou";
		this.MODULE_DESCRIPTION = "从群随机抽取一个人";
		this.MODULE_VERSION = "2.4.1";
		this.MODULE_USAGE = new String[] {
				"//chou - 随机抽一个人",
				"//chou 理由 - 以某个理由抽一个人"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人",
				"获取群成员列表"
		};
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		SecureRandom random = new SecureRandom();
		Member member;
		List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
		int size = members.size();
		if (size < 3) {
			Module.gropInfo(gropid, userid, "至少需要三个成员");
		} else {
			long uid = 0;
			do {
				member = members.get(random.nextInt(size));
				uid = member.getQqId();
			} while ((uid == entry.MYSELFID()) || (uid == userid));
			if (message.segment == 1) {
				Module.gropInfo(gropid, userid, "随机抽到 " + member.getNick() + "(" + uid + ")");
			} else {
				Module.gropInfo(gropid, userid, "随机抽到 " + member.getNick() + "(" + uid + ")： " + message.join(1));
			}
		}
		return true;
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		return null;
	}

}
