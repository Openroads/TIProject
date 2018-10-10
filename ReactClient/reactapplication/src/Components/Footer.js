import React from 'react';

class Footer extends React.Component{

    getYear()
    {
        return new Date().getFullYear();
    }

    render(){
        return(
            <footer className="page-footer font-small blue">
                <div className="footer-copyright text-center py-3">
                    ©  {this.getYear()} Copyright: Urszula Lis, Dariusz Szyszlak, Piotr Makowiec
                </div>
            </footer>
        );
    }
}

export default Footer;