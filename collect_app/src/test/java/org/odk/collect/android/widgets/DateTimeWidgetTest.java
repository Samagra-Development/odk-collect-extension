package org.odk.collect.android.widgets;

import android.view.View;

import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.DateTimeData;
import org.javarosa.form.api.FormEntryPrompt;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.collect.android.R;
import org.odk.collect.android.formentry.questions.QuestionDetails;
import org.odk.collect.android.listeners.WidgetValueChangedListener;
import org.odk.collect.android.logic.DatePickerDetails;
import org.odk.collect.android.utilities.DateTimeUtils;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.mockValueChangedListener;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.promptWithQuestionDefAndAnswer;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.promptWithReadOnlyAndQuestionDef;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.widgetTestActivity;

@RunWith(RobolectricTestRunner.class)
public class DateTimeWidgetTest {

    private QuestionDef questionDef;
    private View.OnLongClickListener listener;

    private LocalDateTime date;
    private DateTime dateTime;
    private LocalDateTime localDateTime;

    @Before
    public void setUp() {
        questionDef = mock(QuestionDef.class);
        listener = mock(View.OnLongClickListener.class);

        date = new LocalDateTime().withYear(2010).withMonthOfYear(5).withDayOfMonth(12);
        dateTime = new DateTime().withTime(12, 0, 0, 0);

        localDateTime = new LocalDateTime()
                .withYear(2010)
                .withMonthOfYear(5)
                .withDayOfMonth(12)
                .withHourOfDay(12)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    @Test
    public void usingReadOnlyOption_doesNotShowButton() {
        DateTimeWidget widget = createWidget(promptWithReadOnlyAndQuestionDef(questionDef));

        assertEquals(widget.binding.dateButton.getVisibility(), View.GONE);
        assertEquals(widget.binding.timeButton.getVisibility(), View.GONE);
    }

    @Test
    public void getAnswer_whenPromptDoesNotHaveAnswer_returnsNull() {
        assertThat(createWidget(promptWithQuestionDefAndAnswer(questionDef, null)).getAnswer(), nullValue());
    }

    @Test
    public void getAnswer_whenPromptHasAnswer_returnsAnswer() {
        DateTimeWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, new DateTimeData(localDateTime.toDate())));
        assertEquals(widget.getAnswer().getDisplayText(), new DateTimeData(localDateTime.toDate()).getDisplayText());
    }

    @Test
    public void whenPromptDoesNotHaveAnswer_answerTextViewShowsNoDateSelected() {
        DateTimeWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));

        assertEquals(widget.binding.dateAnswerText.getText(), widget.getContext().getString(R.string.no_date_selected));
        assertEquals(widget.binding.timeAnswerText.getText(), widget.getContext().getString(R.string.no_time_selected));
    }

    @Test
    public void whenPromptHasAnswer_answerTextViewShowsCorrectDate() {
        FormEntryPrompt prompt = promptWithQuestionDefAndAnswer(questionDef, new DateTimeData(localDateTime.toDate()));
        DatePickerDetails datePickerDetails = DateTimeUtils.getDatePickerDetails(prompt.getQuestion().getAppearanceAttr());
        DateTimeWidget widget = createWidget(prompt);

        assertEquals(widget.binding.dateAnswerText.getText(),
                DateTimeUtils.getDateTimeLabel(date.toDate(), datePickerDetails, false, widget.getContext()));
        assertEquals(widget.binding.timeAnswerText.getText(), "12:00");
    }

    @Test
    public void clearAnswer_clearsWidgetAnswer() {
        DateTimeWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, new DateTimeData(localDateTime.toDate())));
        widget.clearAnswer();

        assertThat(widget.getAnswer(), nullValue());
        assertEquals(widget.binding.dateAnswerText.getText(), widget.getContext().getString(R.string.no_date_selected));
        assertEquals(widget.binding.timeAnswerText.getText(), widget.getContext().getString(R.string.no_time_selected));
    }

    @Test
    public void clearAnswer_callValueChangeListener() {
        DateTimeWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, new DateTimeData(localDateTime.toDate())));
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.clearAnswer();

        verify(valueChangedListener).widgetValueChanged(widget);
    }

    @Test
    public void clickingButtonForLong_callsLongClickListener() {
        DateTimeWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));
        widget.setOnLongClickListener(listener);

        widget.binding.dateButton.performLongClick();
        widget.binding.timeButton.performLongClick();

        verify(listener).onLongClick(widget.binding.dateButton);
        verify(listener).onLongClick(widget.binding.timeButton);
    }

    @Test
    public void clickingAnswerTextViewForLong_callsLongClickListener() {
        DateTimeWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));
        widget.setOnLongClickListener(listener);

        widget.binding.dateAnswerText.performLongClick();
        widget.binding.timeAnswerText.performLongClick();

        verify(listener).onLongClick(widget.binding.dateAnswerText);
        verify(listener).onLongClick(widget.binding.timeAnswerText);
    }

    private DateTimeWidget createWidget(FormEntryPrompt prompt) {
        return new DateTimeWidget(widgetTestActivity(), new QuestionDetails(prompt, "formAnalyticsID"));
    }
}
