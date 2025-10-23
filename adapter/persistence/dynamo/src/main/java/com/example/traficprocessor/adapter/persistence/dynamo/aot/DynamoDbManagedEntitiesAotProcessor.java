package com.example.traficprocessor.adapter.persistence.dynamo.aot;

import static com.example.traficprocessor.adapter.persistence.dynamo.DynamoDbEntityScanner.findDynamoDbEntities;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

final class DynamoDbManagedEntitiesAotProcessor implements BeanFactoryInitializationAotProcessor {
  private static final String MANAGED_ENTITIES_BEAN_NAME = "dynamoDbManagedEntities";

  @Override
  public BeanFactoryInitializationAotContribution processAheadOfTime(
      ConfigurableListableBeanFactory beanFactory) {
    if (beanFactory instanceof BeanDefinitionRegistry registry) {
      var newBeanDefinition =
          rootBeanDefinition(DynamoDbManagedEntities.class)
              .addConstructorArgValue(findDynamoDbEntities())
              .getBeanDefinition();
      registry.removeBeanDefinition(MANAGED_ENTITIES_BEAN_NAME);
      registry.registerBeanDefinition(MANAGED_ENTITIES_BEAN_NAME, newBeanDefinition);
    }

    return null;
  }
}
