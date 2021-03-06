package codes.goblom.spark.conversation.functions;

import codes.goblom.spark.conversation.ConversationStep;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
public abstract class YesNoFunction extends ConversationStep {

    protected static final String[] ACCEPTED = {
                                                    "true", "false",
                                                    "on", "off",
                                                    "yes", "no"
                                                };
    @Override
    public boolean isValid(ConversationContext context, String input) {
        return ArrayUtils.contains(ACCEPTED, input);
    }
}
