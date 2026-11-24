package team.foodlog.backend.service.meal.recognition;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FoodRecognitionRequestedEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(FoodRecognitionRequestedEvent event) {
        publisher.publishEvent(event);
    }

}
