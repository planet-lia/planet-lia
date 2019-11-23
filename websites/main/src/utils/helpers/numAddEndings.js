export const numAddEndings = (num) => {
  num = Number(num);
  let res;
  let lastDigit = num%10;
  let last2Digits = num%100;
  
  if(last2Digits===11 || last2Digits===12 || lastDigit===13){
    res = "th"
  } else if(lastDigit===1){
    res = "st";
  } else if(lastDigit===2){
    res = "nd"
  } else if(lastDigit===3){
    res = "rd"
  } else {
    res = "th";
  }

  return (num.toString() + res)
};
