import React from 'react';

export const countdownRenderer = ({ days, hours, minutes, seconds }) => {
    return (
        <div className="countdown">
            <div>
                <div className="cd-num">{days}</div>
                <div className="cd-title">Days</div>
            </div>
            <div>
                <div className="cd-num">{hours}</div>
                <div className="cd-title">Hours</div>
            </div>
            <div>
                <div className="cd-num">{minutes}</div>
                <div className="cd-title">Minutes</div>
            </div>
            <div>
                <div className="cd-num">{seconds}</div>
                <div className="cd-title">Seconds</div>
            </div>
        </div>
    );
};
