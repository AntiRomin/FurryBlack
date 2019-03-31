package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_zhan extends FunctionModuel {

	private static TreeMap<Integer, String> CARD = new TreeMap<Integer, String>();
	private static ArrayList<Integer> FREQ = new ArrayList<Integer>();

	public Module_zhan() {
		this.MODULE_NAME = "塔罗牌占卜";
		this.MODULE_HELP = "//zhan - 抽取一张大阿卡那牌\r\n//zhan 目的 - 为某事占卜";
		this.MODULE_COMMAND = "zhan";
		this.MODULE_VERSION = "1.2.2";
		this.MODULE_DESCRIPTION = "大阿卡那塔罗牌占卜";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 无\r\n获取 : 1\r\n1: 命令发送人用于@";

		Module_zhan.CARD.put(1, "O. THE FOOL 愚者正位\r\n愚蠢 狂躁 挥霍无度 神志不清");
		Module_zhan.CARD.put(2, "O. THE FOOL 愚者逆位\r\n疏忽 缺乏 暮气 无效 虚荣");
		Module_zhan.CARD.put(3, "I. THE MAGICIAN 魔术师正位\r\n手段 灾难 痛苦 损失");
		Module_zhan.CARD.put(4, "I. THE MAGICIAN 魔术师逆位\r\n羞辱 忧虑 精神疾病");
		Module_zhan.CARD.put(5, "II. THE HIGH PRIESTESS 女祭司正位\r\n秘密 神秘 未来不明朗 英明");
		Module_zhan.CARD.put(6, "II. THE HIGH PRIESTESS 女祭司逆位\r\n冲动 狂热 自负 浮于表面");
		Module_zhan.CARD.put(7, "III. THE EMPRESS 皇后正位\r\n丰收 倡议 隐秘 困难 无知");
		Module_zhan.CARD.put(8, "III. THE EMPRESS 皇后逆位\r\n光明 真相 喜悦");
		Module_zhan.CARD.put(9, "IV. THE EMPEROR 皇帝正位\r\n稳定 力量 帮助 保护 信念");
		Module_zhan.CARD.put(10, "IV. THE EMPEROR 皇帝逆位\r\n仁慈 同情 赞许 阻碍 不成熟");
		Module_zhan.CARD.put(11, "V. THE HIEROPHANT 教皇正位\r\n宽恕 束缚 奴役 灵感");
		Module_zhan.CARD.put(12, "V. THE HIEROPHANT 教皇逆位\r\n善解人意 和睦 过度善良 软弱");
		Module_zhan.CARD.put(13, "VI. THE LOVERS 恋人正位\r\n吸引 爱 美丽 通过试炼");
		Module_zhan.CARD.put(14, "VI. THE LOVERS 恋人逆位\r\n失败 愚蠢的设计");
		Module_zhan.CARD.put(15, "VII. THE CHARIOT 战车正位\r\n救助 天意 胜利 复仇");
		Module_zhan.CARD.put(16, "VII. THE CHARIOT 战车逆位\r\n打败 狂暴 吵架 诉讼");
		Module_zhan.CARD.put(17, "VIII. THE STRENGTH 力量正位\r\n能量 行动 勇气 海量");
		Module_zhan.CARD.put(18, "VIII. THE STRENGTH 力量逆位\r\n专断 弱点 滥用力量 不和");
		Module_zhan.CARD.put(19, "IX. THE HERMIT 隐者正位\r\n慎重 叛徒 掩饰 堕落 恶事");
		Module_zhan.CARD.put(20, "IX. THE HERMIT 隐者逆位\r\n隐蔽 害怕 伪装 过分小心");
		Module_zhan.CARD.put(21, "X. THE WHEEL OF FORTUNE 命运之轮正位\r\n命运 好运 成功 幸福");
		Module_zhan.CARD.put(22, "X. THE WHEEL OF FORTUNE 命运之轮逆位\r\n增加 丰富 多余");
		Module_zhan.CARD.put(23, "XI. THE JUSTICE 正义正位\r\n公平 正义 廉洁 行政");
		Module_zhan.CARD.put(24, "XI. THE JUSTICE 正义逆位\r\n偏执 不公 过度俭朴");
		Module_zhan.CARD.put(25, "XII. THE HANGED MAN 吊人正位\r\n智慧 牺牲 审判 细心 眼光");
		Module_zhan.CARD.put(26, "XII. THE HANGED MAN 吊人逆位\r\n自私 群众 人民");
		Module_zhan.CARD.put(27, "XIII. DEATH 死亡正位\r\n终结 死亡 毁灭 腐朽");
		Module_zhan.CARD.put(28, "XIII. DEATH 死亡逆位\r\n惯性 昏睡 石化 梦游");
		Module_zhan.CARD.put(29, "XIV. TEMPERANCE 节制正位\r\n经济 适度 节俭 管理 住所");
		Module_zhan.CARD.put(30, "XIV. TEMPERANCE 节制逆位\r\n教会 分离 不幸的组合 冲突的利益");
		Module_zhan.CARD.put(31, "XV. THE DEVIL 恶魔正位\r\n毁坏 暴力 强迫 愤怒 额外努力 死亡");
		Module_zhan.CARD.put(32, "XV. THE DEVIL 恶魔逆位\r\n死亡 弱点 盲目 琐事");
		Module_zhan.CARD.put(33, "XVI. THE TOWER 高塔正位\r\n苦难 废墟 贫乏 耻辱 灾害 逆境 骗局");
		Module_zhan.CARD.put(34, "XVI. THE TOWER 高塔逆位\r\n专断 监禁 受苦 损害");
		Module_zhan.CARD.put(35, "XVII. THE STAR 星星正位\r\n丢失 窃贼 匮乏 放弃 未来的希望");
		Module_zhan.CARD.put(36, "XVII. THE STAR 星星逆位\r\n傲慢 无能 傲气");
		Module_zhan.CARD.put(37, "XVIII. THE MOON 月亮正位\r\n隐藏的敌人 诽谤 危险 黑暗 恐怖 错误");
		Module_zhan.CARD.put(38, "XVIII. THE MOON 月亮逆位\r\n不稳定 易变 骗局 错误");
		Module_zhan.CARD.put(39, "XIX. THE SUN 太阳正位\r\n喜悦 结婚 满意");
		Module_zhan.CARD.put(40, "XIX. THE SUN 太阳逆位\r\n开心 满意");
		Module_zhan.CARD.put(41, "XX. THE LAST JUDGMENT 审判正位\r\n变位 复兴 结果");
		Module_zhan.CARD.put(42, "XX. THE LAST JUDGMENT 审判逆位\r\n弱点 胆怯 天真 决定 熟虑");
		Module_zhan.CARD.put(43, "XXI. THE WORLD 世界正位\r\n成功 道路 航程 换位");
		Module_zhan.CARD.put(44, "XXI. THE WORLD 世界逆位\r\n惯性 固执 停滞 持久");
		// 为什么不写循环？ 因为运行快
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		final SecureRandom random = new SecureRandom();
		final int urandom = random.nextInt(43) + 1;
		final StringBuilder builder = new StringBuilder();
		builder.append("你因为");
		builder.append(flow.join(1));
		builder.append(" 抽到了\r\n");
		builder.append(Module_zhan.CARD.get(urandom));
		Module_zhan.FREQ.set(urandom, Module_zhan.FREQ.get(urandom) + 1);
		switch (flow.from) {
		case 1:
			FunctionModuel.priInfo(flow, builder.toString());
			break;
		case 2:
			FunctionModuel.disInfo(flow, builder.toString());
			break;
		case 3:
			FunctionModuel.grpInfo(flow, builder.toString());
			break;
		}
	}

	@Override
	public String genReport() {
		if (this.counter == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		int coverage = 0;
		for (int i = 0; i < 44; i++) {
			if (Module_zhan.FREQ.get(i) == 0) {
				coverage++;
			}
		}
		coverage = 44 - coverage;
		for (int i = 0; i < 44; i++) {
			if (Module_zhan.FREQ.get(i) == 0) {
				continue;
			}
			builder.append("\r\n第 ");
			builder.append(i + 1);
			builder.append(" 张 : ");
			builder.append(Module_zhan.FREQ.get(i));
			builder.append(" - ");
			builder.append((Module_zhan.FREQ.get(i) * 100) / coverage);
			builder.append("%");
		}
		return builder.toString();
	}
}
