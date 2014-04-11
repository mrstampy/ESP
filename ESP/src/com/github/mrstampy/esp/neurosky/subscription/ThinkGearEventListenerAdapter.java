package com.github.mrstampy.esp.neurosky.subscription;

import com.github.mrstampy.esp.mutliconnectionsocket.event.AbstractMultiConnectionEvent;
import com.github.mrstampy.esp.neurosky.event.AbstractThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.BlinkStrengthThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.EEGPowerThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.ESenseThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.EventType;
import com.github.mrstampy.esp.neurosky.event.PoorSignalLevelThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.RawEEGThinkGearEvent;

/**
 * Convenience class that converts the {@link AbstractThinkGearEvent} to its
 * concrete subclass for processing. Override the protected methods in
 * subclasses as appropriate.
 * 
 * @author burton
 * @see ThinkGearSocketConnector
 */
public class ThinkGearEventListenerAdapter implements ThinkGearEventListener {

  @Override
  public final void thinkGearEventPerformed(AbstractMultiConnectionEvent<EventType> e) {
    EventType eventType = e.getEventType();

    switch (eventType) {
    case blinkStrength:
      blinkStrengthEventPerformed((BlinkStrengthThinkGearEvent) e);
      break;
    case eSense:
      eSenseEventPerformed((ESenseThinkGearEvent) e);
      break;
    case eegPower:
      eegPowerEventPerformed((EEGPowerThinkGearEvent) e);
      break;
    case poorSignalLevel:
      poorSignalEventPerformed((PoorSignalLevelThinkGearEvent) e);
      break;
    case rawEeg:
      rawEEGEventPerformed((RawEEGThinkGearEvent) e);
      break;
    default:
      break;
    }
  }

  protected void rawEEGEventPerformed(RawEEGThinkGearEvent e) {
    // blank
  }

  protected void poorSignalEventPerformed(PoorSignalLevelThinkGearEvent e) {
    // blank
  }

  protected void eegPowerEventPerformed(EEGPowerThinkGearEvent e) {
    // blank
  }

  protected void eSenseEventPerformed(ESenseThinkGearEvent e) {
    // blank
  }

  protected void blinkStrengthEventPerformed(BlinkStrengthThinkGearEvent e) {
    // blank
  }

}
