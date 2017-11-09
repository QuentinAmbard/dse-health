import React from "react";
import Dropzone from 'react-dropzone'

function Disk(props) {
    return <div className="input-group" key={props.index}>
              <span className="input-group-addon">
                <input type="checkbox" aria-label="disk-id"/>
              </span>
        <input type="text" className="form-control" aria-label="disk-id" value={props.disk.name} onChange={props.onChange}/>
        { props.index > 0 &&
            <button type="button" class="btn btn-info" onClick={props.onRemoveDisk}>Remove disk</button>
        }
    </div>
}

class Greeter extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            //installFolder: '/etc/dse',
            installFolder: '/home/quentin/dse/dse-5.0.5',
            logFolder: '/var/log/cassandra',
            tmpFolder: '/tmp/dse-health',
            logFormat: '',
            ips: ['127.0.0.1'],
            ipsStr: '127.0.0.1',
            disks: [
                {name: 'nvme0n1', ssd: true}
            ]
        };
        this.onIpsChange.bind(this);
        this.onChange.bind(this);
        this.handleDiskNameChange.bind(this);
        this.removeDisk.bind(this);
    }

    onIpsChange(event) {
        this.state.ipsStr = event.target.value.replace(/\s/g, '');
        this.state.ips = event.target.value.split(",").replace(/\s/g, '');
    }

    handleDiskNameChange(event, index) {
        const disks = this.state.disks;
        disks[index].name = event.target.value;
        this.setState({disks,});
    }

    addDisk(event) {
        console.log(event);
        const disks = this.state.disks;
        disks.push({name: "nvme0n1", ssd: true});
        this.setState({disks,});
    }

    removeDisk(index) {
        const disks = this.state.disks;
        disks.splice(index, 1);
        this.setState({disks,});
    }

    onChange(event) {
        this.setState({disk: {name: event.target.value}})
    }

    generateBashScript() {
        var script = `mkdir -p ${this.state.tmpFolder}/127.0.0.1/logs
cp ${this.state.installFolder}/resources/cassandra/conf/cassandra.yaml ${this.state.tmpFolder}/127.0.0.1/cassandra.yaml
cp ${this.state.installFolder}/resources/dse/conf/dse.yaml ${this.state.tmpFolder}/127.0.0.1/dse.yaml
cp ${this.state.installFolder}/log/cassandra/system.log* ${this.state.tmpFolder}/127.0.0.1/logs/
#remove password & sensitive configuration from configuration files
sed -i 's/^\\(.*password.*\)\:.*$/\\1: xxxxx/g' ${this.state.tmpFolder}/127.0.0.1/cassandra.yaml
sed -i 's/^\\(.*password.*\)\:.*$/\\1: xxxxx/g' ${this.state.tmpFolder}/127.0.0.1/dse.yaml
#analyze keyspaces and system keyspaces
cqlsh -e 'select * from system_schema.keyspaces' > ${this.state.tmpFolder}/127.0.0.1/system_schema.keyspaces
cqlsh -e "copy system.local to '${this.state.tmpFolder}/127.0.0.1/system.local' "
cqlsh -e 'desc schema' > ${this.state.tmpFolder}/127.0.0.1/schema.cql
#cfstats
nodetool cfstats > ${this.state.tmpFolder}/127.0.0.1/cfstats
nodetool status > ${this.state.tmpFolder}/127.0.0.1/status
nodetool info > ${this.state.tmpFolder}/127.0.0.1/info
nodetool tablehistograms xxx
#check os settings:
sysctl -a > ${this.state.tmpFolder}/127.0.0.1/sysctl
#check zone_reclaim_mode
cat /proc/sys/vm/zone_reclaim_mode > ${this.state.tmpFolder}/127.0.0.1/zone_reclaim_mode
#check disks settings:
mkdir -p ${this.state.tmpFolder}/127.0.0.1/disks
`;
        this.state.disks.map((disk) => {
            script += `mkdir ${this.state.tmpFolder}/127.0.0.1/disks/${disk.name}
cat /sys/block/${disk.name}/queue/scheduler > ${this.state.tmpFolder}/127.0.0.1/disks/${disk.name}/scheduler
cat /sys/block/${disk.name}/queue/read_ahead_kb > ${this.state.tmpFolder}/127.0.0.1/disks/${disk.name}/read_ahead_kb
cat /sys/block/${disk.name}/queue/rotational > ${this.state.tmpFolder}/127.0.0.1/disks/${disk.name}/rotational
echo ${disk.ssd}> ${this.state.tmpFolder}/127.0.0.1/disks/${disk.name}/ssd
`
        })
        script +=`#disable transparent huge pages
#check limits
export PID=\`ps aux | grep com.datastax.bdp.DseModule | grep java | awk '{print $2}'\`
cat "/proc/$PID/limits" > ${this.state.tmpFolder}/127.0.0.1/limits
strings "/proc/$PID/environ" > ${this.state.tmpFolder}/127.0.0.1/environ
strings "/proc/$PID/cmdline" > ${this.state.tmpFolder}/127.0.0.1/cmdline

tar -czvf ${this.state.tmpFolder}/report-127.0.0.1.tar.gz -C ${this.state.tmpFolder} 127.0.0.1

#on the main host
mkdir -p /tmp/dse-health
scp 127.0.0.1:${this.state.tmpFolder}/report-127.0.0.1.tar.gz ${this.state.tmpFolder}/report-127.0.0.1.tar.gz`
        return script
    }

    onDrop(files){
        var data = new FormData();
        data.append('analysis', files[0])
        //data.append('user', 'hubot')

        fetch('/api/upload', {
            method: 'POST',
            body: data
        }).then(response => console.log(response))
    }


    render() {
        return (
            <div>
                <form>
                    <div className="form-group">
                        <label htmlFor="tmpFolder">Nodes ip (for ssh connection, will be used as node identifier)</label>
                        <input type="text" className="form-control" id="tmpFolder" value={this.state.ips} onChange={event => this.setState({ips: event.target.value})}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="tmpFolder">Tmp folder (on each server)</label>
                        <input type="text" className="form-control" id="tmpFolder" value={this.state.ipsStr} onChange={event => this.setState({tmpFolder: event.target.value})}/>
                        cleanup: <input type="checkbox" className="form-control" id="cleanup" value={this.state.cleanup} onChange={event => this.setState({cleanup: event.target.cleanup})}/>
                    </div>

                    disks:
                    <button type="button" class="btn btn-info" onClick={() => this.addDisk()}>Add disk</button>
                    {this.state.disks.map((disk, index) => {
                        return <div>
                            <Disk index={index} disk={this.state.disks[index]} onChange={(event) => this.handleDiskNameChange(event, index)} onRemoveDisk={() => this.removeDisk(index)} />
                        </div>
                    })}

                    <div className="form-group">
                        <label htmlFor="installFolder">Install folder</label>
                        <input type="text" className="form-control" id="installFolder" value={this.state.installFolder} onChange={event => this.setState({installFolder: event.target.value})}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="logFormat">Log format (default matches to<code>&lt;pattern&gt;%-5level [%thread] %date{'{ISO8601}'} %X{'{service}'} %F:%L - %msg%n </code>)</label>
                        See http://grok.nflabs.com/WhatIsPattern for more detail on log <pattern></pattern>
                        <input type="text" className="form-control" id="logFormat" value={this.state.logFormat} onChange={event => this.setState({logFormat: event.target.value})}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="logFolder">Log folder</label>
                        <input type="text" className="form-control" id="logFolder" value={this.state.logFolder} onChange={event => this.setState({logFolder: event.target.value})}/>
                    </div>
                    <textarea value={this.generateBashScript()} rows="10" cols="100" ></textarea>
                </form>
            <Dropzone onDrop={this.onDrop}>
                <div>Try dropping some files here, or click to select files to upload.</div>
            </Dropzone>
        </div>

        )
    }
}

export default Greeter;
