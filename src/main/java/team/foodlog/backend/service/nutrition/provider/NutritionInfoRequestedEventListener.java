package team.foodlog.backend.service.nutrition.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NutritionInfoRequestedEventListener {

    private final NutritionInfoRequestedEventProcessor processor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NutritionInfoRequestedEvent event) {
        processor.process(event);
    }

}
