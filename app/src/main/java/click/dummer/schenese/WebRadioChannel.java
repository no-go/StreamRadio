package click.dummer.schenese;

public class WebRadioChannel implements Comparable<WebRadioChannel>
{
  private String radioName;
  private String radioUrl;
  private boolean selected = true;
  
  public WebRadioChannel(String radioName, String radioUrl)
  {
    this.radioName = radioName;
    this.radioUrl = radioUrl;
  }

  public String getName()
  {
    return radioName;
  }
  
  public String getFullName()
  {
    return radioName;
  }
  
  public String getUrl()
  {
    return radioUrl;
  }
  
  /** @return Whether this channel is selected as Favourite. **/
  public boolean isSelected()
  {
    return selected;
  }

  /** @param selected Whether this channel is selected as Favourite. **/
  public void setSelected(boolean selected)
  {
    this.selected = selected;
  }

  @Override
  public int compareTo(WebRadioChannel other)
  {
    if (other instanceof WebRadioChannel)
    {
      WebRadioChannel otherW = (WebRadioChannel)other;
      int cmp = radioName.compareTo(otherW.radioName);
      if (cmp!=0) { return cmp; }
      return radioUrl.compareTo(otherW.radioUrl);
    }
    return 1;
  }

  @Override
  public boolean equals(Object other)
  {
    if (other instanceof WebRadioChannel)
    {
      WebRadioChannel otherW = (WebRadioChannel) other;
      return compareTo(otherW)==0;
    }
    return false;
  }
  
  public String toString() { return radioName + "\n" + radioUrl; }

  public void setData(WebRadioChannel newChannel)
  {
    radioName = newChannel.radioName;
    radioUrl = newChannel.radioUrl;
    selected = newChannel.selected;
  }
}
