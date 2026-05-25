package com.postech.techchallenge.fase3.hospital.notificacao;

import com.postech.techchallenge.fase3.hospital.notificacao.config.TestRabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@Import(TestRabbitMQConfig.class)
class NotificacaoApplicationTests {

	@Test
	void contextLoads() {
	}

}