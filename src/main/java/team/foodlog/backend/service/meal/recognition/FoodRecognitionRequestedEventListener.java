package team.foodlog.backend.service.meal.recognition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FoodRecognitionRequestedEventListener {

    private final FoodRecognitionRequestedEventProcessor foodRecognitionRequestedEventProcessor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FoodRecognitionRequestedEvent event) {
        foodRecognitionRequestedEventProcessor.process(event);
    }
}
