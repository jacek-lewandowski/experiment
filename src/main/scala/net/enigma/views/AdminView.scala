package net.enigma.views

import scala.languageFeature.implicitConversions

import com.vaadin.ui._

import net.enigma.TextResources
import net.enigma.Utils._

/**
 * @author Jacek Lewandowski
 */
trait AdminView extends AbstractView {
  val GROUP_COL = "group"
  val NOT_STARTED_COL = "notStarted"
  val NOT_FINISHED_COL = "notFinished"
  val FINISHED_COL = "finished"

  val summaryTable = new Table()
  summaryTable.addContainerProperty(GROUP_COL, classOf[String], null,
    TextResources.Labels.AdminSummaryTableGroupCol, null, Table.Align.LEFT)
  summaryTable.addContainerProperty(NOT_STARTED_COL, classOf[Integer], null,
    TextResources.Labels.AdminSummaryTableNotStartedCol, null, Table.Align.LEFT)
  summaryTable.addContainerProperty(NOT_FINISHED_COL, classOf[Integer], null,
    TextResources.Labels.AdminSummaryTableNotFinishedCol, null, Table.Align.LEFT)
  summaryTable.addContainerProperty(FINISHED_COL, classOf[Integer], null,
    TextResources.Labels.AdminSummaryTableFinishedCol, null, Table.Align.LEFT)

  val generateGroupButton = new Button(TextResources.Labels.AdminGenerateGroupButton)
  val generateUserButton = new Button(TextResources.Labels.AdminGenerateUserButton)
  val groupNameField = new TextField(TextResources.Labels.AdminGroupName)
  groupNameField.setNullRepresentation("")

  val generateOptionsPanel = new Panel(new HorizontalLayout(
    generateGroupButton,
    generateUserButton,
    groupNameField
  ).withSpacing.withMargins
    .withComponentAlignment(generateGroupButton, Alignment.BOTTOM_CENTER)
    .withComponentAlignment(generateUserButton, Alignment.BOTTOM_CENTER)
    .withComponentAlignment(groupNameField, Alignment.BOTTOM_CENTER))

  val generatedCodesTextArea = new TextArea(TextResources.Labels.AdminGeneratedCodes).withSizeFull
  generatedCodesTextArea.setNullRepresentation("")

  val summaryPanel = new Panel(TextResources.Labels.AdminSummaryTable, summaryTable.withSizeFull).withSizeFull

  val generatePanel = new Panel(TextResources.Labels.AdminGeneratePanel,
    new VerticalLayout(generateOptionsPanel, generatedCodesTextArea.withSizeFull)
      .withSpacing.withMargins.withSizeFull.withExpandRatio(generatedCodesTextArea, 1)).withSizeFull

  val mainLayout = new HorizontalLayout(summaryPanel, generatePanel).withSpacing.withMargins
  mainLayout.setExpandRatio(summaryPanel, 0.5f)
  mainLayout.setExpandRatio(generatePanel, 0.5f)
  mainLayout.setSizeFull()
  content.addComponent(mainLayout)


  layout.removeComponent(top)
  layout.removeComponent(bottom)
}
