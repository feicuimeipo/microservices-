package com.nx.boot.launch.spi;



import com.nx.boot.launch.env.NxBootstrap;
import org.springframework.core.Ordered;
import java.util.Properties;

/**
 * launcher 扩展 用于一些组件发现
 */
public interface NxBootProcessor extends Ordered, Comparable<NxBootProcessor>  {

	/**
	 * 启动时 处理自定义处理SpringApplicationBuilder
	 */
	void launcher(String applicationName, NxBootstrap bootstrap, Properties props, Class mainClass);


	/**
	/**
	 * 获取排列顺序
	 *
	 * @return order
	 */
	@Override
	default int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	/**
	 * 对比排序
	 *
	 * @param o LauncherService
	 * @return compare
	 */
	@Override
	default int compareTo(NxBootProcessor o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}


}
