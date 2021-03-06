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

package org.flowable.form.engine.impl.cmd;

import java.io.InputStream;
import java.io.Serializable;

import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.form.engine.impl.persistence.entity.FormDefinitionEntity;
import org.flowable.form.engine.impl.util.CommandContextUtil;

/**
 * Gives access to a deployed form model, e.g., a Form JSON file, through a stream of bytes.
 * 
 * @author Tijs Rademakers
 */
public class GetFormDefinitionResourceCmd implements Command<InputStream>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String formDefinitionId;

    public GetFormDefinitionResourceCmd(String formDefinitionId) {
        if (formDefinitionId == null || formDefinitionId.length() < 1) {
            throw new FlowableIllegalArgumentException("The form definition id is mandatory, but '" + formDefinitionId + "' has been provided.");
        }
        this.formDefinitionId = formDefinitionId;
    }

    public InputStream execute(CommandContext commandContext) {
        FormDefinitionEntity formDefinition = CommandContextUtil.getFormEngineConfiguration().getDeploymentManager()
                .findDeployedFormDefinitionById(formDefinitionId);

        String deploymentId = formDefinition.getDeploymentId();
        String resourceName = formDefinition.getResourceName();
        InputStream formDefinitionStream = new GetDeploymentResourceCmd(deploymentId, resourceName).execute(commandContext);
        return formDefinitionStream;
    }

}
