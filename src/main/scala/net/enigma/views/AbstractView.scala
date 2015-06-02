package net.enigma.views

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent
import com.vaadin.server.Page
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import org.slf4j.LoggerFactory

import net.enigma.Utils._
import net.enigma.{App, ExperimentUI, TextResources}

/**
 * @author Jacek Lewandowski
 */
trait AbstractView extends Layout with View {

  setCaption(title)
  Page.getCurrent.setTitle(title)

  private val logger = LoggerFactory.getLogger("AbstractView")

  val nextButton = new Button(TextResources.Labels.NextButton, { _: Button.ClickEvent ⇒ next() }).withSizeUndefined

  val top = new HorizontalLayout().withSpacing.withFullWidth
  val content = new VerticalLayout().withSpacing.withSizeFull
  val bottom = new HorizontalLayout(nextButton).withFullWidth.withComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT)
  private val _top = new VerticalLayout(top).withSpacing.withFullWidth

  val layout = new VerticalLayout(_top, content, bottom).withMargins.withSpacing.withSizeFull.withExpandRatio(content, 1)

  setSizeFull()
  addComponent(layout)
  setCaption(title)

  def addTitle(): Unit = {
    val titleLayout = new HorizontalLayout(new Label(title).withContentMode(ContentMode.HTML)).withSizeUndefined
    _top.addComponent(titleLayout, 0)
    _top.setComponentAlignment(titleLayout, Alignment.TOP_CENTER)
  }

  def addInfo(info: String): Unit = {
    val instructionField = new Label(info)
        .withFullWidth
        .withContentMode(ContentMode.HTML)
    val contentPanel = new Panel(new VerticalLayout(instructionField).withSpacing.withMargins)
        .withSizeFull

    _top.addComponent(contentPanel, 0)
    _top.setComponentAlignment(contentPanel, Alignment.TOP_CENTER)
  }

  def ui = getUI match {
    case ui: ExperimentUI => ui
  }

  def navigateTo(view: String) = {
    logger.info(s"Navigating to $view")
    ui.navigator.navigateTo(view)
    Page.getCurrent.setUriFragment(view, false)
  }

  def title: String

  def allowedToEnter: Boolean

  def entered(event: ViewChangeEvent): Unit

  def next(): Unit

  def accept(): Boolean

  def failedToEnter(event: ViewChangeEvent): Unit

  override def enter(event: ViewChangeEvent): Unit = {
    setCaption(TextResources.Titles.Main)
    if (allowedToEnter) {
      logger.info(s"Entering ${this.getClass.getName}")
      entered(event)
    } else {
      logger.info(s"Failed to enter ${this.getClass.getName}")
      failedToEnter(event)
    }
  }

}
