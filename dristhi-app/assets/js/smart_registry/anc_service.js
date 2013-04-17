angular.module("smartRegistry.services")
    .service('ANCService', function () {
        var schedules =
            [
                {
                    "name":"anc",
                    "milestones":["anc1", "anc2", "anc3", "anc4"]
                },
                {
                    "name":"tt",
                    "milestones":["tt1", "tt2"]
                },
                {
                    "name":"ifa",
                    "milestones":["ifa1", "ifa2"]
                }
            ];

        var alert_status = {
            NORMAL:"normal",
            URGENT:"urgent",
            DONE:"done",
            UPCOMING:"upcoming"
        };

        var status_css_class = {};
        status_css_class[alert_status.NORMAL] = "btn-due";
        status_css_class[alert_status.URGENT] = "btn-past-due";
        status_css_class[alert_status.DONE] = "btn-done";
        status_css_class[alert_status.UPCOMING] = "btn-upcoming";

        if(Array.prototype.find === undefined)
        {
            Array.prototype.find = function(func)
            {
                var idx;
                for(var i = 0; i < this.length; i++)
                {
                    if(func.call(this, this[i]))
                    {
                        idx = i;
                        break;
                    }
                }
                if(idx !== undefined)
                {
                    return this[i];
                }
            };
        }

        return {
            getClients:function () {
                var clients = [
                    {
                        village:'Chikkabherya',
                        name:'Carolyn',
                        thayi:'4636587',
                        ec_number:'314',
                        age:'24',
                        husband_name:'Billy Bob',
                        weeks_pregnant:'18',
                        edd:'2012-06-11T00:00:00.000Z',
                        lmp:'25/3/13',
                        alerts:[
                            {
                                name:'anc1',
                                date:'24/05',
                                status:'urgent'
                            },
                            {
                                name:'anc2',
                                date:'24/07',
                                status:'urgent'
                            },
                            {
                                name:'tt1',
                                date:'26/05',
                                status:'normal'
                            },
                            {
                                name:'ifa1',
                                date:'26/05',
                                status:'normal'
                            }
                        ],
                        anc_visits:[
                            {visit:'1', date:'04/04'},
                            {visit:'2', date:'04/08'},
                            {visit:'3', date:'04/09'}
                        ],
                        tt:[
                            {tt:'1', date:'04/04'},
                            {tt:'2', date:'04/08'}
                        ],
                        ifa:{dose:'100', date:'04/04'},
                        days_due:'3',
                        due_message:'Follow Up',
                        isHighPriority:false,
                        locationStatus:"out_of_area"
                    },
                    {
                        village:'Chikkabherya',
                        name:'Roger',
                        thayi:'4636587',
                        ec_number:'314',
                        age:'24',
                        husband_name:'Jacck',
                        weeks_pregnant:'24',
                        edd:'2012-04-11T00:00:00.000Z',
                        lmp:'25/3/13',
                        alerts:[
                            {
                                name:'anc2',
                                date:'24/07',
                                status:'normal'
                            },
                            {
                                name:'tt1',
                                date:'26/05',
                                status:'done'
                            }
                        ],
                        anc_visits:[
                            {visit:'1', date:'04/04'},
                            {visit:'2', date:'04/08'}
                        ],
                        tt:[
                            {tt:'1', date:'04/04'},
                            {tt:'2', date:'04/08'}
                        ],
                        ifa:{dose:'100', date:'04/04'},
                        days_due:'3',
                        due_message:'Follow Up',
                        isHighPriority:true,
                        locationStatus:"left_the_place"
                    },
                    {
                        village:'Bherya',
                        name:'Larry',
                        thayi:'4636587',
                        ec_number:'314',
                        age:'24',
                        husband_name:'Dickson',
                        weeks_pregnant:'2',
                        edd:'2013-05-11T00:00:00.000Z',
                        lmp:'25/3/13',
                        alerts:[
                            {
                                name:'anc3',
                                date:'24/05',
                                status:'normal'
                            },
                            {
                                name:'ifa2',
                                date:'26/05',
                                status:'done'
                            },
                            {
                                name:'ifa1',
                                date:'26/03',
                                status:'done'
                            }
                        ],
                        anc_visits:[
                            {visit:'1', date:'04/04'},
                            {visit:'2', date:'04/08'}
                        ],
                        tt:[
                            {tt:'1', date:'04/04'},
                            {tt:'2', date:'04/08'}
                        ],
                        ifa:{dose:'100', date:'04/04'},
                        days_due:'3',
                        due_message:'Follow Up',
                        isHighPriority:false,
                        locationStatus:"in_area"
                    },
                    {
                        village:'Bherya',
                        name:'Ukanga',
                        thayi:'4636587',
                        ec_number:'315',
                        age:'27',
                        husband_name:'Harshit',
                        weeks_pregnant:'2',
                        edd:'2013-05-11T00:00:00.000Z',
                        lmp:'25/3/13',
                        alerts:[
                            {
                                name:'anc4',
                                date:'24/05',
                                status:'done'
                            },
                            {
                                name:'anc2',
                                date:'24/07',
                                status:'done'
                            },
                            {
                                name:'tt1',
                                date:'26/05',
                                status:'done'
                            },
                            {
                                name:'tt2',
                                date:'26/05',
                                status:'normal'
                            }
                        ],
                        anc_visits:[
                            {visit:'1', date:'04/04'},
                            {visit:'2', date:'04/08'}
                        ],
                        tt:[
                            {tt:'1', date:'04/04'},
                            {tt:'2', date:'04/08'}
                        ],
                        ifa:{dose:'100', date:'04/04'},
                        days_due:'3',
                        due_message:'Follow Up',
                        isHighPriority:false,
                        locationStatus:"in_area"
                    }

                ];

                return clients;
            },
            preProcessClients:function (clients) {
                clients.forEach(function (client) {
                        var visits = {};
                        schedules.forEach(function (schedule) {
                            var visit = {};
                            var alertsOfTypeCurrentSchedule = [];
                            alertsOfTypeCurrentSchedule = client.alerts.filter(function (alert) {
                                return schedule.milestones.indexOf(alert.name) > -1;
                            });

                            for (var i = schedule.milestones.length - 1; i > -1; i--) {
                                var milestone = schedule.milestones[i];
                                var milestone_alert = alertsOfTypeCurrentSchedule.find(function (schedule_alert) {
                                    return schedule_alert.name === milestone;
                                });
                                if (milestone_alert !== undefined) {
                                    if (milestone_alert.status === alert_status.NORMAL || milestone_alert.status === alert_status.URGENT) {
                                        visit.next = milestone;
                                        var next_milestone = {};
                                        next_milestone.status = status_css_class[milestone_alert.status];
                                        next_milestone.visit_date = milestone_alert.date;
                                        visit[milestone] = next_milestone;

                                        if(i > 0)
                                        {
                                            for(var prev_idx = i; prev_idx > 0; prev_idx--){
                                                var prev_alert = alertsOfTypeCurrentSchedule.find(function(milestone_alert){
                                                   return milestone_alert.name === schedule.name + prev_idx;
                                                });

                                                if(prev_alert !== undefined)
                                                {
                                                    visit.previous = prev_alert.name;
                                                    var previous_milestone = {};
                                                    previous_milestone.status = status_css_class[prev_alert.status];
                                                    previous_milestone.visit_date = prev_alert.date;
                                                    visit[visit.previous] = previous_milestone;
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    else if(milestone_alert.status === alert_status.DONE)
                                    {
                                        // check if we are on the last milestone
                                        if(i === schedule.milestones.length - 1)
                                        {
                                            visit.next = milestone_alert.name;
                                            var next_milestone = {};
                                            next_milestone.status = status_css_class[milestone_alert.status];
                                            next_milestone.visit_date = milestone_alert.date;
                                            visit[visit.next] = next_milestone;

                                            // find previous
                                            for(var prev_idx = i; prev_idx > 0; prev_idx--){
                                                prev_alert = alertsOfTypeCurrentSchedule.find(function(milestone_alert){
                                                    return milestone_alert.name === schedule.name + prev_idx;
                                                });

                                                if(prev_alert !== undefined)
                                                {
                                                    visit.previous = prev_alert.name;
                                                    var previous_milestone = {};
                                                    previous_milestone.status = status_css_class[prev_alert.status];
                                                    previous_milestone.visit_date = prev_alert.date;
                                                    visit[visit.previous] = previous_milestone;
                                                    break;
                                                }
                                            }
                                        }
                                        else if(i < schedule.milestones.length - 1)
                                        {
                                            visit.next = schedule.milestones[schedule.milestones.indexOf(milestone) + 1];
                                            var next_milestone = {};
                                            next_milestone.status = status_css_class[alert_status.UPCOMING];
                                            next_milestone.visit_date = null;
                                            visit[visit.next] = next_milestone;

                                            visit.previous = milestone_alert.name;
                                            var previous_milestone = {};
                                            previous_milestone.status = status_css_class[milestone_alert.status];
                                            previous_milestone.visit_date = milestone_alert.date;
                                            visit[visit.previous] = previous_milestone;
                                        }
                                        break;
                                    }
                                }
                            }
                            if(visit.next === undefined)
                            {
                                visit.next = schedule.milestones[0];
                                var next_milestone = {};
                                next_milestone.status = status_css_class[alert_status.UPCOMING];
                                next_milestone.visit_date = null;
                                visit[visit.next] = next_milestone;
                            }
                            visits[schedule.name] = visit;
                        });
                        client.visits = visits;
                    }
                );
                return clients;
            }
        }
    })
;