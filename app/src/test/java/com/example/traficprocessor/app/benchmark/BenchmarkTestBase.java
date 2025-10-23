package com.example.traficprocessor.app.benchmark;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.openjdk.jmh.results.format.ResultFormatType.JSON;
import static org.openjdk.jmh.results.format.ResultFormatType.valueOf;
import static org.springframework.core.io.support.PropertiesLoaderUtils.loadAllProperties;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@BenchmarkIntegrationTest
abstract class BenchmarkTestBase {
  private static final DateTimeFormatter formatter = ofPattern("yyyy-mm-dd_hh-mm-ss");
  // Forking is disabled so benchmark methods can access @Autowired fields
  private static final int FORKS = 0;

  @BenchmarkTest
  public void executeJmhRunner() throws RunnerException, IOException {
    var properties = loadAllProperties("benchmark.properties");
    var warmup = Integer.parseInt(properties.getProperty("warmup.iterations", "5"));
    var iterations = Integer.parseInt(properties.getProperty("test.iterations", "5"));
    var threads = Integer.parseInt(properties.getProperty("test.threads", "1"));
    var resultFilePrefix = properties.getProperty("result.file_prefix", getClass().getSimpleName());
    var resultFormat = valueOf(properties.getProperty("result.format", JSON.name()));

    var options =
        new OptionsBuilder()
            .include("\\." + this.getClass().getSimpleName() + "\\.")
            .warmupIterations(warmup)
            .measurementIterations(iterations)
            .warmupTime(TimeValue.NONE)
            .measurementTime(TimeValue.NONE)
            .forks(FORKS)
            .threads(threads)
            .shouldDoGC(true)
            .shouldFailOnError(true)
            .resultFormat(resultFormat)
            .result(toResultFileName(resultFilePrefix, resultFormat))
            .shouldFailOnError(true)
            .jvmArgs("-server")
            .build();

    new Runner(options).run();
  }

  private static String toResultFileName(String resultFilePrefix, ResultFormatType resultFormat) {
    var suffix =
        switch (resultFormat) {
          case CSV -> ".csv";
          case SCSV -> ".scsv";
          case LATEX -> ".tex";
          case JSON -> ".json";
          case TEXT -> ".txt";
        };

    return String.format("target/%s-%s%s", resultFilePrefix, now().format(formatter), suffix);
  }
}
