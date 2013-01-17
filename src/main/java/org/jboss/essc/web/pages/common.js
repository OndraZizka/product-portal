
/**
 *  Toggles display: none | block;
 */
function toggleVisibility(elm){
    if( elm.style.display === 'none' )
        elm.style.display = 'block';
    else
        elm.style.display = 'none';
}