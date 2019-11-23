
export function parseGameStatistics (gameStatistics, player1Name, player2Name) {
    // Build data
    let labels = [];
    let powers = [[], []];
    let workers = [[], []];
    let warriors = [[], []];
    let units = [[], []];
    let resources = [[], []];

    let pushResourcePoint = function (i, prevValue, currentValue) {
        let nGatheredResources = (resources[i].length === 0) ? 0 : resources[i][resources[i].length - 1];

        if (prevValue < currentValue) {
            resources[i].push(nGatheredResources + (currentValue - prevValue))
        } else {
            resources[i].push(nGatheredResources)
        }
    };

    for (let i = 0; i < gameStatistics.length; i++) {
        let s = gameStatistics[i];
        labels.push(Math.floor(s.time));
        powers[0].push(s.team1.power);
        powers[1].push(s.team2.power);

        workers[0].push(s.team1.workers);
        workers[1].push(s.team2.workers);

        warriors[0].push(s.team1.warriors);
        warriors[1].push(s.team2.warriors);

        units[0].push(s.team1.warriors + s.team1.workers);
        units[1].push(s.team2.warriors + s.team2.workers);

        pushResourcePoint(
            0,
            (i === 0) ? 0 : gameStatistics[i-1].team1.resources,
            s.team1.resources
        );
        pushResourcePoint(
            1,
            (i === 0) ? 0 : gameStatistics[i-1].team2.resources,
            s.team2.resources
        );
    }


    // Power graph
    let powerData = {
        labels: labels,
        datasets: [
            getDatasetOptions(0, player1Name, powers[0]),
            getDatasetOptions(1, player2Name, powers[1])
        ]
    };
    let resourceData = {
        labels: labels,
        datasets: [
            getDatasetOptions(0, player1Name, resources[0]),
            getDatasetOptions(1, player2Name, resources[1])
        ]
    };
    let workersData = {
        labels: labels,
        datasets: [
            getDatasetOptions(0, player1Name, workers[0]),
            getDatasetOptions(1, player2Name, workers[1])
        ]
    };
    let warriorsData = {
        labels: labels,
        datasets: [
            getDatasetOptions(0, player1Name, warriors[0]),
            getDatasetOptions(1, player2Name, warriors[1])
        ]
    };

    let unitsData = {
        labels: labels,
        datasets: [
            getDatasetOptions(0, player1Name, units[0]),
            getDatasetOptions(1, player2Name, units[1])
        ]
    };

    return {
        powerData: powerData,
        resourceData: resourceData,
        workersData: workersData,
        warriorsData: warriorsData,
        unitsData: unitsData,
    }
}


function getDatasetOptions(playerId, playerName, data) {
    let color = playerId ? "#019170": "#D9C72E";
    return {
        label: playerName,
        fill: false,
        lineTension: 0.1,
        backgroundColor: color,
        borderColor: color,
        borderCapStyle: 'butt',
        borderDash: [],
        borderDashOffset: 0.0,
        borderJoinStyle: 'miter',
        pointBorderColor: color,
        pointBackgroundColor: color,
        pointBorderWidth: 1,
        pointHoverRadius: 5,
        pointHoverBackgroundColor: color,
        pointHoverBorderColor: color,
        pointHoverBorderWidth: 2,
        pointRadius: 1,
        pointHitRadius: 10,
        data: data
    };
}
