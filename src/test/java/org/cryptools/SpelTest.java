package org.cryptools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelTest {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SpelTest.class);
	
	@Test
	public void test() {

	ExpressionParser parser = new SpelExpressionParser();
	TemplateParserContext templateParserContext = new TemplateParserContext();
	StandardEvaluationContext context = new StandardEvaluationContext();
	context.addPropertyAccessor(new MapAccessor());
	
	//bean has 'name' property
	Bean bean = new Bean("myname");
	
	Map<String,Object> map = MapUtils.putAll(new HashMap<>(),new Object[]{"w", "world","b",bean,"nested", MapUtils.putAll(new HashMap<>(),new String[]{"key","others"})});
	Expression expression = parser.parseExpression("hello #{w} from #{b.name} and #{nested.key}", templateParserContext);
	String result = (String) expression.getValue(context, map);
	
	//result is: 'hello world from myname and others'
	LOGGER.info("result: {}",result);
		
		//Object o = expr.getValue();
		//assertThat(o.toString()).isEqualTo("hello world");
		
	}

	public static class Bean {
		String name;
		
		public Bean(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
