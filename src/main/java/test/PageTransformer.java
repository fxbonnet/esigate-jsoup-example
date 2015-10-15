/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package test;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.Extension;
import org.esigate.impl.DriverRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms the pages using Jsoup.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class PageTransformer implements Extension, IEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(PageTransformer.class);

    @Override
    public boolean event(EventDefinition id, Event event) {

        RenderEvent renderEvent = (RenderEvent) event;
        
        // Add the Renderer that is going to rewrite the html code
        renderEvent.getRenderers().add(new Renderer() {

            @Override
            public void render(DriverRequest request, String html, Writer writer) throws IOException, HttpErrorPage {
                Document document = Jsoup.parse(html);
                String currentTitle = document.title();
                String newTitle = "new title";
                LOG.info("Replacing page title '" + currentTitle + "' with '" + newTitle + "'");
                document.title(newTitle);
                writer.write(document.html());
            }
        });

        // Continue processing
        return true;
    }

    @Override
    public void init(Driver driver, Properties properties) {
        driver.getEventManager().register(EventManager.EVENT_RENDER_PRE, this);
    }

}
