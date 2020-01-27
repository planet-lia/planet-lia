

export function seconds2time(secondsRaw) {
  secondsRaw = Math.round(secondsRaw);

  let hours   = Math.floor(secondsRaw / 3600);
  let minutes = Math.floor((secondsRaw - (hours * 3600)) / 60);
  let seconds = secondsRaw - (hours * 3600) - (minutes * 60);
  let time = "";

  if (hours !== 0) {
    time = hours+":";
  }
  if (minutes !== 0 || time !== "") {
    minutes = (minutes < 10 && time !== "") ? "0"+minutes : String(minutes);
    time += minutes+":";
  }
  if (time === "") {
    time = seconds+"s";
  }
  else {
    time += (seconds < 10) ? "0"+seconds : String(seconds);
  }
  return time;
}

export function timeSince(date) {
  return timeDiff(new Date() - date);
}

export function timeTo(date) {
  return timeDiff(date - new Date());
}

function timeDiff(date) {
  let seconds = Math.floor(date / 1000);

  let interval = Math.floor(seconds / 31536000);

  if (interval > 1) {
    return interval + " years";
  }
  interval = Math.floor(seconds / 2592000);
  if (interval > 1) {
    return interval + " months";
  }
  interval = Math.floor(seconds / 86400);
  if (interval > 1) {
    return interval + " days";
  }
  interval = Math.floor(seconds / 3600);
  if (interval > 1) {
    return interval + " hours";
  }
  interval = Math.floor(seconds / 60);
  if (interval > 1) {
    return interval + " minutes";
  }
  return Math.floor(seconds) + " seconds";
}
