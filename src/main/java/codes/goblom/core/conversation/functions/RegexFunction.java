package codes.goblom.core.conversation.functions;

import java.util.regex.Pattern;
import codes.goblom.core.conversation.ConversationStep;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
public abstract class RegexFunction extends ConversationStep {
    
    public abstract Pattern getPattern();
    
    @Override
    public boolean isValid(ConversationContext context, String input) {
        return getPattern().matcher(input).matches();
    }
}
