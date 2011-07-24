/*
* Copyright 2011 Wro4J
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package ro.isdc.wro.extensions.model.factory

import org.junit.Test
import ro.isdc.wro.model.WroModel
import ro.isdc.wro.model.resource.ResourceType

/**
 * Test Wro Groovy DSL
 *
 * @author Filirom1
 * @created 19 Jul 2011
 */
class TestGroovyWroModelParser {

  @Test
  public void testResourceDelegate() {
    //setup:
    def resourceDelegate = new ResourceDelegate()

    //when:
    resourceDelegate.js("js/*.js")
    resourceDelegate.css(minimize: false, "css/*.css")
    resourceDelegate.css(minimize: true, "css/style.css")

    //then:
    assert 3 == resourceDelegate.resources.size()

    assert resourceDelegate.resources[0].minimize
    assert !resourceDelegate.resources[1].minimize
    assert resourceDelegate.resources[2].minimize

    assert ResourceType.JS == resourceDelegate.resources[0].type
    assert ResourceType.CSS == resourceDelegate.resources[1].type
    assert ResourceType.CSS == resourceDelegate.resources[2].type

    assert "js/*.js" == resourceDelegate.resources[0].uri
    assert "css/*.css" == resourceDelegate.resources[1].uri
    assert "css/style.css" == resourceDelegate.resources[2].uri
  }

  @Test
  public void testGroupDelegate() {
    //setup:
    def groupDelegate = new GroupDelegate()

    //when:
    groupDelegate.g1 {
      js("/js/script.js")
      css("/css/style.css")
    }
    groupDelegate.g2 {
      js("/js/script2.js")
    }

    //then:
    assert 2 == groupDelegate.groups.size()
    assert "g1" == groupDelegate.groups[0].name
    assert 2 == groupDelegate.groups[0].resources.size()
    assert "g2" == groupDelegate.groups[1].name
    assert 1 == groupDelegate.groups[1].resources.size()
  }

  @Test
  public void testWroModelDelegate() {
    //setup:
    def wroModelDelegate = new WroModelDelegate()

    //when:
    wroModelDelegate.groups {
      g1 {
        js("/js/script.js")
        css("/css/style.css")
      }
      g2 {
        js("/js/script2.js")
      }
    }

    //then:
    assert 2 == wroModelDelegate.wroModel.groups.size()
    assert 2 == wroModelDelegate.wroModel.groups.find {it.name == "g1"}.resources.size()
    assert 1 == wroModelDelegate.wroModel.groups.find {it.name == "g2"}.resources.size()
  }

  @Test
  public void testParse() {
    //setup:
    def dsl = """
    groups {
      g1 {
        js("/js/script.js")
        css("/css/style.css")
      }
      g2 {
        js("/js/script2.js")
      }
    }
    """

    //when:
    WroModel wroModel = GroovyWroModelParser.parse(dsl)

    //then:
    assert ["g2", "g1"] == wroModel.groupNames
  }
}