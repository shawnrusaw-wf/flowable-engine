/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.engine.common.impl.interceptor;

import org.flowable.engine.common.impl.cfg.TransactionContext;
import org.flowable.engine.common.impl.cfg.TransactionContextFactory;
import org.flowable.engine.common.impl.cfg.TransactionListener;
import org.flowable.engine.common.impl.context.Context;

/**
 * @author Joram Barrez
 */
public class TransactionContextInterceptor extends AbstractCommandInterceptor {

    protected TransactionContextFactory transactionContextFactory;

    public TransactionContextInterceptor() {
    }

    public TransactionContextInterceptor(TransactionContextFactory transactionContextFactory) {
        this.transactionContextFactory = transactionContextFactory;
    }

    public <T> T execute(CommandConfig config, Command<T> command) {

        CommandContext commandContext = Context.getCommandContext();
        // Storing it in a variable, to reference later (it can change during command execution)
        boolean isReused = commandContext.isReused();
        boolean isContextSet = false;

        try {

            if (transactionContextFactory != null && !isReused) {
                TransactionContext transactionContext = (TransactionContext) transactionContextFactory.openTransactionContext(commandContext);
                Context.setTransactionContext(transactionContext);
                isContextSet = true;
                commandContext.addCloseListener(new TransactionCommandContextCloseListener(transactionContext));
            }

            return next.execute(config, command);

        } finally {
            if (transactionContextFactory != null && isContextSet && !isReused) {
                Context.removeTransactionContext();
            }
        }

    }

    public TransactionContextFactory getTransactionContextFactory() {
        return transactionContextFactory;
    }

    public void setTransactionContextFactory(TransactionContextFactory transactionContextFactory) {
        this.transactionContextFactory = transactionContextFactory;
    }

}
