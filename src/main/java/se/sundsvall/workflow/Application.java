package se.sundsvall.workflow;

import se.sundsvall.dept44.ServiceApplication;

import static org.springframework.boot.SpringApplication.run;

@ServiceApplication
public class Application {
  public static void main(String... args) {
    run(Application.class, args);
  }
}