package mobile.jira.clonejira;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootTest
class ClonejiraApplicationTests {
	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Lấy tất cả tên Bean đang được load trong Context
		String[] beanNames = applicationContext.getBeanDefinitionNames();

		// Sắp xếp và in ra để kiểm tra
		Arrays.sort(beanNames);
		System.out.println("--- DANH SÁCH BEAN ĐÃ LOAD ---");
		for (String beanName : beanNames) {
			// Chỉ in các bean của project (lọc bớt bean hệ thống của Spring cho gọn)
			if (beanName.contains("Controller") || beanName.contains("Service") || beanName.contains("Repository")) {
				System.out.println(beanName);
			}
		}
		System.out.println("------------------------------");
	}

}
