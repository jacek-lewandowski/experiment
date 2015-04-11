package net.enigma.views.components

import scala.collection.JavaConversions._

import com.vaadin.data.Property
import com.vaadin.data.util.IndexedContainer
import com.vaadin.ui._
import org.slf4j.LoggerFactory
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroup

/**
 * @author Jacek Lewandowski
 */
class SurveyOptionGroup[T](
  optionsLayout: ComponentContainer with Layout,
  layoutCreator: (Component, Component) ⇒ Component,
  itemLayoutCreator: (Component, Component) ⇒ Component,
  options: Seq[(String, T)],
  caption: String,
  required: Boolean
) extends Panel {

  import net.enigma.views.components.SurveyOptionGroup._

  private val logger = LoggerFactory.getLogger(classOf[SurveyOptionGroup[_]])

  val container = new IndexedContainer()
  container.addContainerProperty(CaptionProperty, classOf[String], null)
  for ((optName, optValue) ← options) {
    val item = container.addItem(optValue)
    val prop = item.getItemProperty(CaptionProperty).asInstanceOf[Property[String]]
    prop.setValue(optName)
  }

  val optionGroup = new FlexibleOptionGroup(container)
  optionGroup.setRequired(required)

  for (itemComponent ← optionGroup.getItemComponentIterator) {
    val itemLayout = itemLayoutCreator(new Label(itemComponent.getCaption), itemComponent)
    optionsLayout.addComponent(itemLayout)
  }

  val captionLabel = new Label(caption)
  val layout = layoutCreator(captionLabel, optionsLayout)

  setContent(layout)

  def value: T = optionGroup.getValue.asInstanceOf[T]

  def validate(): Unit = {
    optionGroup.validate()
  }

}

object SurveyOptionGroup {
  val CaptionProperty = "caption"

}
