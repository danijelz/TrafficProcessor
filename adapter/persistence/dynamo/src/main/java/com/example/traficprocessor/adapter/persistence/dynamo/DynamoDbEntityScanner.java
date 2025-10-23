package com.example.traficprocessor.adapter.persistence.dynamo;

import com.example.traficprocessor.adapter.persistence.dynamo.entity.TableName;
import io.vavr.control.Try;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

public final class DynamoDbEntityScanner {
  public static List<Class<?>> findDynamoDbEntities() {
    var provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AnnotationTypeFilter(DynamoDbBean.class));
    return provider.findCandidateComponents(TableName.class.getPackageName()).stream()
        .map(BeanDefinition::getBeanClassName)
        .<Class<?>>map(bn -> Try.of(() -> Class.forName(bn)).get())
        .toList();
  }
}
