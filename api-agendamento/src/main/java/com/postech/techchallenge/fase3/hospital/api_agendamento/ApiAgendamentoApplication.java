package com.postech.techchallenge.fase3.hospital.api_agendamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiAgendamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiAgendamentoApplication.class, args);
	}

}