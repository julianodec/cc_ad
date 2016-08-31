/**
 * ReactJS component representing the active campaign view.
 */
var ActiveCampaignList = React.createClass({
	render: function() {
		var activeCampaign = this.props.data;
		var campaignNodes = (!activeCampaign) ? 'No active campaign found.' : <Campaign key={activeCampaign.id} id={activeCampaign.id} active={activeCampaign.active} duration={activeCampaign.duration} isOdd={false}>
			{activeCampaign.ad_content}
		</Campaign>;
		return (
			<div className="campaignList panel">
				<h2>Existing active campaign for partner</h2>
				<ul className="campaigns">
					{campaignNodes}
				</ul>
			</div>
		);
	}
});

/**
 * Component representing the list of all campaigns for a partner.
 */
var CampaignList = React.createClass({
	render: function() {
		var onCampaignActivate = this.props.onCampaignActivate;
		var i = -1;
		var campaignNodes = this.props.data.map(function(campaign) {
			i++;
			return (
				<Campaign key={campaign.id} id={campaign.id} active={campaign.active} duration={campaign.duration} onCampaignActivate={onCampaignActivate} isOdd={i % 2 == 1}>
					{campaign.ad_content}
				</Campaign>
			);
		});
		return (
			<div className="campaignList panel">
				<h2>List of existing campaigns for partner</h2>
				<ul className="campaigns">
					{campaignNodes}
				</ul>
			</div>
		);
	}
});

/**
 * Component representing the form used to add a new campaign.
 */
var CampaignForm = React.createClass({
	getInitialState: function() {
		return { active: false, duration: 10, ad_content: '' };
	},

	handleActiveChange: function(e) {
		this.setState({active: e.target.checked});
	},

	handleDurationChange: function(e) {
		this.setState({duration: e.target.value});
	},

	handleAdContentChange: function(e) {
		this.setState({ad_content: e.target.value});
	},

	handleSubmit: function(e) {
		e.preventDefault();
		var partnerId = this.props.campaignBox.state.partner_id;
		var active = this.state.active;
		var duration = this.state.duration;
		var adContent = this.state.ad_content.trim();
		if (!partnerId) {
			alert("Please select a partner before continuing.");
			return;
		}
		if (!duration) {
				alert("Please set a duration for the campaign before continuing.");
				return;
			}
		if (!adContent) {
			alert("Please add the ad content first before continuing.");
			return;
		}
		this.props.onCampaignSubmit({active: active, ad_content: adContent, partner_id: partnerId, duration: duration});
		this.setState({active: false, duration: 10, ad_content: ''});
	},

	render: function() {
		return (
			<div className="campaignForm panel">
				<h2>Add a new campaign for partner</h2>
				<form onSubmit={this.handleSubmit}>
					<div className="field">
						<label htmlFor="active">Is campaign active:</label>
						<input id="active" type="checkbox" checked={this.state.active} onClick={this.handleActiveChange} />
					</div>
					<div className="field">
						<label htmlFor="duration">Duration (seconds):</label>
						<input type="text" id="duration" required placeholder="Duration in seconds..." value={this.state.duration} onChange={this.handleDurationChange} />
					</div>
					<div className="field">
						<label htmlFor="content">Content:</label>
						<textarea id="content" required rows="5" placeholder="Add new campaign content here..." value={this.state.ad_content} onChange={this.handleAdContentChange} />
					</div>
					<div className="field">
						<input type="submit" value="Save" />
					</div>
				</form>
			</div>
		);
	}
});

/**
 * Parent component in charge of all logic across its children components.
 */
var CampaignBox = React.createClass({
	handlePartnerChange: function(e) {
		this.setState({partner_id: e.target.value});
	},

	loadCampaignsFromServer: function() {
		if(this.state.partner_id > 0){
			$.ajax({
				url: this.props.listUrl + this.state.partner_id,
				dataType: 'json',
				cache: false,
				success: function(data) {
					this.setState({allCampaigns: data});
				}.bind(this),
				error: function(xhr, status, err) {
					console.error(this.props.url, status, err.toString());
				}.bind(this)
			});
		}
	},

	loadActiveCampaignFromServer: function() {
		if(this.state.partner_id > 0){
			$.ajax({
				url: this.props.activeUrl + this.state.partner_id,
				dataType: 'json',
				cache: false,
				success: function(data) {
					this.setState({activeCampaign: data});
				}.bind(this),
				error: function(xhr, status, err) {
					var error = JSON.parse(xhr.responseText);
					if(error) {
					if(error.status == 204) {
							this.setState({activeCampaign: null});
					} else {
							alert("Error: " + error.msg);
						}
				}
				}.bind(this)
			});
		}
	},

	handleCampaignSubmit: function(campaign) {
		$.ajax({
			url: this.props.addUrl,
			dataType: 'json',
			type: 'POST',
			data: JSON.stringify(campaign),
			contentType: 'application/json',
			success: function(data) {
				//no-op
			}.bind(this),
			error: function(xhr, status, err) {
				var error = JSON.parse(xhr.responseText);
				if(error && error.error) {
					alert("Error: " + error.msg);
				}
			}.bind(this)
		});
	},

	handleCampaignActivate: function(campaignId) {
		$.ajax({
			url: this.props.activateUrl + campaignId,
			dataType: 'json',
			type: 'PUT',
			contentType: 'application/json',
			success: function(data) {
				this.setState({data: data});
			}.bind(this),
			error: function(xhr, status, err) {
				console.error(this.props.url, status, err.toString());
			}.bind(this)
		});
	},

	getInitialState: function() {
		return {activeCampaign: null, allCampaigns: [], partner_id: 0 };
	},

	componentDidMount: function() {
		this.loadCampaignsFromServer();
		this.loadActiveCampaignFromServer();
		setInterval(this.loadCampaignsFromServer, this.props.pollInterval);
		setInterval(this.loadActiveCampaignFromServer, this.props.pollInterval);
	},
	render: function() {
		return (
			<div className="campaignBox">
				<h1 className="header">Everyone Loves Ads!</h1>
				<div className="partnerSelect panel">
					<h2>To start, please select a partner:</h2>
					<form>
						<select id="partner" onChange={this.handlePartnerChange} value={this.state.selectValue}>
							<option value="0">Please select a partner</option>
							<option value="10">INFINITY TV</option>
							<option value="20">INFINITY Business</option>
						</select>
					</form>
				</div>
				<CampaignForm onCampaignSubmit={this.handleCampaignSubmit} campaignBox={this} />
				<ActiveCampaignList data={this.state.activeCampaign} campaignBox={this} />
				<CampaignList onCampaignActivate={this.handleCampaignActivate} data={this.state.allCampaigns} campaignBox={this} />
			</div>
		);
	}
});

/**
 * ReactJS class representing a campaign, and its state.
 */
var Campaign = React.createClass({
	render: function() {
		var activateUrl = "/ad/activate/" + this.props.id;
		var link = (this.props.active) ? '' : <span><a href="#" onClick={this.props.onCampaignActivate.bind(null, this.props.id)}>Activate</a></span>;
		return (
			<li className={(this.props.isOdd) ? 'odd' : 'even'}>
				<span className={'isActive' + ((this.props.active) ? ' active' : '')}><label>Active:</label> {this.props.active.toString()}</span>
				<span><label>Duration:</label> {this.props.duration} seconds</span>
				<span><label>Content:</label> {this.props.children}</span>
				{link}
			</li>
		);
	}
});

/**
 * Render all ReactJS components and views.
 */
ReactDOM.render(
	<CampaignBox listUrl="/ad/all/" activeUrl="/ad/" addUrl="/ad" activateUrl="/ad/activate/" pollInterval={2000} />,
	document.getElementById('content')
);
