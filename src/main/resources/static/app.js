let ROOT_PATH = '/kalah';

let Pit = React.createClass({
    render: function () {
        let result;
        let stonesCount;
        let stones;
        let button = (<button onClick={this.onClick}>|</button>);
        if (this.props.player === 'FIRST_PLAYER') {
            stonesCount = this.props.parent.state.firstPits[this.props.pit];
            stones = (<span>{stonesCount}</span>);
            result = (<div>{stones}<br/>{button}</div>);
        } else if (this.props.player === 'SECOND_PLAYER') {
            stonesCount = this.props.parent.state.secondPits[this.props.pit];
            stones = (<span>{stonesCount}</span>);
            result = (<div>{button}<br/>{stones}</div>);
        } else {
            result = (<div>Internal error: Wrong player tag ({this.props.player})</div>);
        }
        console.debug('Pit.render() returns:', result);
        return result;
    },
    onClick: function () {
        this.props.parent.receiveGameResponse(
            axios.put( // make a move
                ROOT_PATH + '/' + this.props.parent.state.gameId + '/' + this.props.parent.state.version + '/' + this.props.player + '/' + (this.props.pit + 1)
            )
        );
    }
});

let KalahGame = React.createClass({
    render: function () {
        console.debug('render');
        let result = (
            <div id='KalahGame'>
                <h1>Kalah Game</h1>
                <br/><br/>
                <button onClick={this.createGame}>Start a new game</button>
                <br/><br/>
                <button onClick={this.joinGame}>Connect to an existing game</button>
                <br/><br/>
                <div>Game #{this.state.gameId}</div>
                <br/><br/>
                {this.renderGameArea()}
                <br/><br/>
                <button onClick={this.refreshCurrentView}
                        hidden={this.state.gameId === undefined || this.state.gameId === null}>
                    Refresh
                </button>
                <br/><br/>
                {this.state.eventLog && this.state.eventLog.map(eventRecord => {
                    if (eventRecord.toLowerCase().indexOf('sorry') >= 0) {
                        return (<p><font color='DarkRed'>{eventRecord}</font></p>)
                    } else if (eventRecord.toLowerCase().indexOf('!') >= 0) {
                        return (<p><font color='DarkGreen'>{eventRecord}</font></p>)
                    } else {
                        return (<p>{eventRecord}</p>);
                    }
                })}
                <br/>
                <span>{new Date().toLocaleTimeString()}</span>
            </div>
        );
        console.debug('KalahGame.render() returns:', result);
        return result;
    },
    renderGameArea: function () {
        console.debug('renderGameArea');
        let result;
        if (this.state && this.state.gameId && this.state.firstPits && this.state.firstPits.length) {

            let secondPlayerPits = [];
            for (let i = this.state.secondPits.length - 1; i >= 0; i--) {
                let pitControl = (
                    <td key={'player1-pit' + i} align="center" valign="middle">
                        <Pit player="SECOND_PLAYER"
                             pit={i}
                             parent={this}
                        />
                    </td>
                );
                secondPlayerPits.push(pitControl);
            }
            let firstPlayerPits = [];
            for (let i = 0; i < this.state.firstPits.length; i++) {
                let pitComponent = (
                    <Pit player="FIRST_PLAYER"
                         pit={i}
                         parent={this}
                    />
                );
                let pitCell = (
                    <td key={'player0-pit' + i} align="center" valign="middle">
                        {pitComponent}
                    </td>
                );
                firstPlayerPits.push(pitCell);
            }
            result = (
                <div width='100%' align='center'>
                    <div height='60px'><font color='Green'>{this.state.secondMessage}</font></div>
                    <table align='center'>
                        <tr>
                            <td rowSpan='3' align="center" valign="middle">
                                <div>{this.state.secondStore}</div>
                            </td>
                            {secondPlayerPits}
                            <td rowSpan='3' align="center" valign="middle">
                                <div>{this.state.firstStore}</div>
                            </td>
                        </tr>
                        <tr>
                            <td colSpan={this.state.numberOfSmallPitsForEachPlayer}>
                                <p><font color='Red'>{this.state.gameOverMessage}</font></p>
                            </td>
                        </tr>
                        <tr>
                            {firstPlayerPits}
                        </tr>
                    </table>
                    <div height='60px'><font color='Green'>{this.state.firstMessage}</font></div>
                </div>
            );
        } else {
            result = <div><font color='LightGrey'>(Game not started yet)</font></div>
        }
        return result;
    },
    createGame: function () {
        this.receiveGameResponse(
            axios.post( // start a new game
                ROOT_PATH
            )
        );
    },
    joinGame: function () {
        let joinGameId = prompt('Please enter the ID of Kalah game you want to join:', '1');
        console.debug(joinGameId);
        if (joinGameId !== null && joinGameId !== undefined && isFinite(joinGameId) && !isNaN(joinGameId)) {
            this.state.gameId = joinGameId;
            this.receiveGameResponse(
                axios.get( // join an existing game
                    ROOT_PATH + '/' + this.state.gameId
                )
            );
        }
    },
    refreshCurrentView: function () {
        this.receiveGameResponse(
            axios.get( // refresh the game view
                ROOT_PATH + '/' + this.state.gameId
            )
        );
    },
    receiveGameResponse: function (promisedResponse) {
        console.debug('receiveGameResponse(', promisedResponse, ')');
        promisedResponse
            .then(response => {
                console.debug(response);
                console.debug(response.data);
                if (response && response.data) {
                    this.setState(response.data, function () {
                        console.debug('setState() callback finished', this.state);
                    });
                } else {
                    console.warn('No response.data received');
                }
            })
            .catch(error => {
                console.error(error);
                alert(error);
                window.location.replace('');
            });
    },
    getInitialState: function () {
        return {
            gameId: undefined
        }
    }
});

ReactDOM.render(
    <div width='100%' align='center'>
        <KalahGame/>
    </div>
    , document.getElementById('root')
);
