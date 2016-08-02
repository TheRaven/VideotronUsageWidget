package com.yannicksimard.videotronusagewidget.usage;

public class Usage {
  private String usage;
  private Long maximumUsage;
  private Long daysFromStart;
  private Long daysToEnd;
  private Long downloadedBytes;
  private Long uploadedBytes;

  public Usage(String usage, Long maximumUsage, Long daysFromStart, Long daysToEnd, Long downloadedBytes, Long uploadedBytes) {
    this.usage = usage;
    this.maximumUsage = maximumUsage;
    this.daysFromStart = daysFromStart;
    this.daysToEnd = daysToEnd;
    this.downloadedBytes = downloadedBytes;
    this.uploadedBytes = uploadedBytes;
  }

  public String getUsage() {
    return usage;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public void setMaximumUsage(Long maximumUsage) {
    this.maximumUsage = maximumUsage;
  }

  public Long getMaximumUsage() {
    return maximumUsage;
  }

  public Long getDaysToEnd() {
    return daysToEnd;

  }

  public void setDaysToEnd(Long daysToEnd) {
    this.daysToEnd = daysToEnd;
  }

  public Long getDaysFromStart() {
    return daysFromStart;
  }

  public void setDaysFromStart(Long daysFromStart) {
    this.daysFromStart = daysFromStart;
  }

  public Long getDownloadedBytes() {
    return downloadedBytes;
  }

  public void setDownloadedBytes(Long downloadedBytes) {
    this.downloadedBytes = downloadedBytes;
  }

  public Long getUploadedBytes() {
    return uploadedBytes;
  }

  public void setUploadedBytes(Long uploadedBytes) {
    this.uploadedBytes = uploadedBytes;
  }
}
